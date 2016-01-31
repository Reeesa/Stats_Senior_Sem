package getData;

import java.io.*;
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
