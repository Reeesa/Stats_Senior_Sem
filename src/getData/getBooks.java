package getData;

import java.io.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class getBooks {
	public static void main(String[] args) throws Exception {

			getBooks bestsellers = new getBooks();
			
			//making and formatting dates
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
			Date startDate = ft.parse("2015-01-01");
			Date endDate = ft.parse("2015-06-01");
			//adding a week to a date
			Date nextWeek = nextWeek(startDate);
			//comparing dates
			if(startDate.compareTo(endDate) < 0){
				//System.out.println("startDate is before endDate");
			}
			
			ArrayList<JSONObject> jsonArr = new ArrayList<JSONObject>();
			Date d = startDate;
			while (d.compareTo(endDate) < 0){
				String url = "http://api.nytimes.com/svc/books/v2/lists.json?list=hardcover-fiction&date=" + ft.format(d) + "&api-key=" + keyHolder.key1;
				jsonArr.add(bestsellers.sendGet(url));
				d = nextWeek(d);
				Thread.sleep(125);
			}
			System.out.println("length of list Array: " + jsonArr.size());
			
			//getting out the data I want for each book -> organizing it so there is only one copy of each book
			//making an array list to store the book objects in
			ArrayList<Book> bookArr = new ArrayList<Book>();
			//looping through the array list of bestsellers lists
			for (int j = 0; j < jsonArr.size(); j++){
				//looping through the all the books in each list
				JSONObject list = jsonArr.get(j);
				JSONArray results = list.getJSONArray("results");
				for (int i = 0; i<20; i++){
					JSONObject currentBook = results.getJSONObject(i);
					Book tempBook = new Book(currentBook);
					//add first book
					if (bookArr.size() == 0){
						bookArr.add(tempBook);
					}
					//check if book already exists
					int index = bookInList(bookArr, tempBook);
					if (index != -1){
						//book already exists
						//check rank, weeks on list
						if (tempBook.highestRank > bookArr.get(index).highestRank){
							bookArr.get(index).highestRank = tempBook.highestRank;
						}
						if (tempBook.mostWeeksOnList > bookArr.get(index).mostWeeksOnList){
							bookArr.get(index).mostWeeksOnList = tempBook.mostWeeksOnList;
						}
					}
					else{
						bookArr.add(tempBook);
					}
				}
			}
			System.out.println("number of books: " + bookArr.size());
			
			//getting data from google books -> looping through bookArr
			ArrayList<JSONObject> googleArr = new ArrayList<JSONObject>();
			for (int w = 0; w < bookArr.size(); w++){
				String title = bookArr.get(w).title;
				title = title.replaceAll("\\s","%20");
				String ISBN = bookArr.get(w).isbn;
				String publisher = bookArr.get(w).publisher;
				publisher = publisher.replaceAll(",",  "");
				publisher = publisher.replaceAll("\\s",  "%20");
				String url = "https://www.googleapis.com/books/v1/volumes?q=" + title + "+intitle:" + title + "+inpublisher:" + publisher + "+isbn:" + ISBN + "&key=" + keyHolder.key3;
				JSONObject result = bestsellers.sendGet(url);
				Thread.sleep(125);
				if (result.getInt("totalItems") == 1){
					JSONArray hold = result.getJSONArray("items");
					result = hold.getJSONObject(0);
					googleArr.add(result);
				}
				else if (result.getInt("totalItems") > 1){
					JSONArray hold = result.getJSONArray("items");
					result = hold.getJSONObject(0);
					googleArr.add(result);
				}
				else if (result.getInt("totalItems") == 0){
					String url2 = "https://www.googleapis.com/books/v1/volumes?q=" + title + "+isbn:" + ISBN + "&key=" + keyHolder.key3;
					result = bestsellers.sendGet(url2);
					Thread.sleep(125);
					if (result.getInt("totalItems") == 1){
						JSONArray hold = result.getJSONArray("items");
						result = hold.getJSONObject(0);
						googleArr.add(result);
					}
					else if (result.getInt("totalItems") > 1){
						JSONArray hold = result.getJSONArray("items");
						result = hold.getJSONObject(0);
						googleArr.add(result);
					}
					else{
						String url3 = "https://www.googleapis.com/books/v1/volumes?q=" + title + "+intitle:" + title + "&key=" + keyHolder.key3;
						result = bestsellers.sendGet(url3);
						Thread.sleep(125);
						JSONArray hold = result.getJSONArray("items");
						result = hold.getJSONObject(0);
						googleArr.add(result);	
					}
				}
			}
			System.out.println("number of google books: " + googleArr.size());
			
			// Time to use the google books array (and the books array) to finish making the book. (Adding on the google books data.)
			for (int i = 0; i < googleArr.size(); i++){
				//all the books will still be in bookArr
				//adding the google data to each book element in bookArr
				Book.finishBook(googleArr.get(i), bookArr.get(i));
				System.out.println("finished a book");
			}
			System.out.println("books are fully created");
			
			// Making the csv file! ALMOST DONE!
			generateCsvFile("C:\\Users\\brockman\\Desktop\\BookSeniorSem.csv", bookArr);
	}
	
	
	public static int bookInList(ArrayList<Book> bookArr, Book tempBook){
		for (int k = 0; k < bookArr.size(); k++){
			if (bookArr.get(k).isbn.equals(tempBook.isbn)){
				return k;
			}
		}
		return -1;
	}
	
	private JSONObject sendGet(String url) throws Exception {
		
		URL asURL = new URL(url);
		HttpURLConnection con = (HttpURLConnection) asURL.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		String responseMessage = con.getResponseMessage();
		//System.out.println("\nSending 'GET' request");
		//System.out.println("Response Code : " + responseCode);
		System.out.println("Response Message : " + responseMessage);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		//System.out.println(response.toString());
		JSONObject json = new JSONObject(response.toString());
		
		return json;
	}
	
	public static Date nextWeek(Date date){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 7);
		Date newDate = date;
		newDate.setTime(calendar.getTime().getTime());
		return newDate;
	}

private static void generateCsvFile(String sFileName, ArrayList<Book> bookArr)
{
	try
	{
	    FileWriter writer = new FileWriter(sFileName);
		
	    System.out.println("making file");
	    
	    // USING FOR LOOPS FOR THE CATEGORIES
	    // fist, making a string array of allllll the categories
	    ArrayList<String> categories = new ArrayList<String>();
	    categories.add("isbn");
	    categories.add("listDate");
	    categories.add("highestRank");
	    categories.add("mostWeeksOnList");
	    categories.add("title");
	    categories.add("author");
	    categories.add("price");
	    categories.add("ageGroup");
	    categories.add("publisher");
	    categories.add("bookURL");
	    categories.add("googTitle");
	    categories.add("googAuths");
	    categories.add("googPublisher");
	    categories.add("googPublishDate");
	    categories.add("googDescription");
	    categories.add("googISBN");
	    categories.add("googModeText");
	    categories.add("googModeImage");
	    categories.add("googPageCount");
	    categories.add("googPrintType");
	    categories.add("googCats");
	    categories.add("googAvgRating");
	    categories.add("googRatingCount");
	    categories.add("googMaturity");
	    categories.add("googLang");
	    categories.add("googCountry");
	    categories.add("googEbook");
	    categories.add("googListPrice");
	    categories.add("googRetailPrice");
	    categories.add("googViewablility");
	    categories.add("googEmbeddable");
	    categories.add("googPublicDomain");
	    categories.add("googTextToSpeech");
	    categories.add("googAccessStatus");
	    categories.add("googQuoteShare");
	    categories.add("googSnippet");
	    
	    // headers
	    for (int i = 0; i < categories.size(); i++){
	    	if(i==0){
	    		writer.append(categories.get(i));
	    	}
	    	else{
	    		writer.append(", ");
	    		writer.append(categories.get(i));
	    	}
	    }
	    writer.append('\n');
	    
	    // books then categories
	    for (int i = 0; i < bookArr.size(); i++){
	    	writer.append(bookArr.get(i).isbn);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).listDate);
	    	writer.append(", ");
	    	writer.append(Integer.toString(bookArr.get(i).highestRank));
	    	writer.append(", ");
	    	writer.append(Integer.toString(bookArr.get(i).mostWeeksOnList));
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).title);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).author);
	    	writer.append(", ");
	    	writer.append(Integer.toString(bookArr.get(i).price));
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).ageGroup);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).publisher);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).bookURL);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googTitle);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googAuths);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googPublisher);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googPublishDate);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googTitle);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googAuths);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googDescription);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googISBN);
	    	writer.append(", ");
	    	writer.append(Boolean.toString(bookArr.get(i).googModeText));
	    	writer.append(", ");
	    	writer.append(Boolean.toString(bookArr.get(i).googModeImage));
	    	writer.append(", ");
	    	writer.append(Integer.toString(bookArr.get(i).googPageCount));
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googPrintType);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googCats);
	    	writer.append(", ");
	    	writer.append(Integer.toString(bookArr.get(i).googAvgRating));
	    	writer.append(", ");
	    	writer.append(Integer.toString(bookArr.get(i).googRatingCount));
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googMaturity);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googLang);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googCountry);
	    	writer.append(", ");
	    	writer.append(Boolean.toString(bookArr.get(i).googEbook));
	    	writer.append(", ");
	    	writer.append(Double.toString(bookArr.get(i).googListPrice));
	    	writer.append(", ");
	    	writer.append(Double.toString(bookArr.get(i).googRetailPrice));
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googViewability);
	    	writer.append(", ");
	    	writer.append(Boolean.toString(bookArr.get(i).googEmbeddable));
	    	writer.append(", ");
	    	writer.append(Boolean.toString(bookArr.get(i).googPublicDomain));
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googTextToSpeech);
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googAccessStatus);
	    	writer.append(", ");
	    	writer.append(Boolean.toString(bookArr.get(i).googQuoteShare));
	    	writer.append(", ");
	    	writer.append(bookArr.get(i).googSnippet);
	    	writer.append('\n');
	    }
	    
	    // Headers
//	    writer.append("isbn");
//	    writer.append(',');
//	    writer.append("listDate");
//	    writer.append(',');
//	    writer.append("highestRank");
//	    writer.append(',');
//	    writer.append("mostWeeksOnList");
//	    writer.append(',');
//	    writer.append("title");
//	    writer.append(',');
//	    writer.append("author");
//	    writer.append(',');
//	    writer.append("price");
//	    writer.append(',');
//	    writer.append("ageGroup");
//	    writer.append(',');
//	    writer.append("publisher");
//	    writer.append(',');
//	    writer.append("bookURL");
//	    writer.append(',');
//	    writer.append("googTitle");
//	    writer.append(',');
//	    writer.append("googAuths");
//	    writer.append(',');
//	    writer.append("googPublisher");
//	    writer.append(',');
//	    writer.append("googPublishDate");
//	    writer.append(',');
//	    writer.append("googDescription");
//	    writer.append(',');
//	    writer.append("googISBN");
//	    writer.append(',');
//	    writer.append("googModeText");
//	    writer.append(',');
//	    writer.append("googModeImage");
//	    writer.append(',');
//	    writer.append("googPageCount");
//	    writer.append(',');
//	    writer.append("googPrintType");
//	    writer.append(',');
//	    writer.append("googCats");
//	    writer.append(',');
//	    writer.append("googAvgRating");
//	    writer.append(',');
//	    writer.append("googRatingCount");
//	    writer.append(',');
//	    writer.append("googMaturity");
//	    writer.append(',');
//	    writer.append("googLang");
//	    writer.append(',');
//	    writer.append("googCountry");
//	    writer.append(',');
//	    writer.append("googEbook");
//	    writer.append(',');
//	    writer.append("googListPrice");
//	    writer.append(',');
//	    writer.append("googRetailPrice");
//	    writer.append(',');
//	    writer.append("googViewablility");
//	    writer.append(',');
//	    writer.append("googEmbeddable");
//	    writer.append(',');
//	    writer.append("googPublicDomain");
//	    writer.append(',');
//	    writer.append("googTextToSpeech");
//	    writer.append(',');
//	    writer.append("googAccessStatus");
//	    writer.append(',');
//	    writer.append("googQuoteShare");
//	    writer.append(',');
//	    writer.append("googSnippet");
//	    writer.append('\n');

	    // data
	    //writer.append("MKYONG");
	    //writer.append(',');
	    //writer.append("26");
        //writer.append('\n');
			
	    writer.flush();
	    writer.close();
	}
	catch(IOException e)
	{
	     e.printStackTrace();
	} 
 }
}





// API request format, NYT Bestsellers:
// GET http://api.nytimes.com/svc/books/v2/lists.json?list=hardcover-fiction&date=2015-11-08&api-key=KEY
// replace KEY with API key
// set date=_____  desired date of list in form YYYY-MM-DD

// Q: Do I need to get the history of the books on the list separately?
// (Total number of weeks on list, etc.)
// MAINLY: Does 'weeks on list' mean in total, or in a row???


// API request format, Google Bestsellers:
// GET https://www.googleapis.com/books/v1/volumes?q=TITLEKEYWORD+isbn=ISBN&key=KEY
// replace KEY with API key
// replace TITLEKEYWORD with title keyword
// replace ISBN with an isbn number

// both api's will give results as a JSON object

// HTTP GET request
