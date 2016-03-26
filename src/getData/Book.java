package getData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class Book {

	// NYT Data
	public String isbn;
	public String listDate;
	public int highestRank;
	public int mostWeeksOnList;
	public String title;
	public String author;
	public int price;
	public String ageGroup;
	public String publisher;
	
	// Google Books Data
	public String bookURL;
	public String googTitle;
	public String googAuths;
	public String googPublisher;
	public String googPublishDate;
	public String googDescription;
	public String googISBN;
	public boolean googModeText;
	public boolean googModeImage;
	public int googPageCount;
	public String googPrintType;
	public String googCats;
	public int googAvgRating;
	public int googRatingCount;
	public String googMaturity;
	public String googLang;
	public String googCountry;
	public boolean googEbook;
	public double googListPrice;
	public double googRetailPrice;
	public String googViewability;
	public boolean googEmbeddable;
	public boolean googPublicDomain;
	public String googTextToSpeech;
	public String googAccessStatus;
	public boolean googQuoteShare;
	public String googSnippet;

	public Book(JSONObject book) throws JSONException {
		listDate = book.getString("bestsellers_date");
		highestRank = book.getInt("rank");
		mostWeeksOnList = book.getInt("weeks_on_list");
		JSONArray details = book.getJSONArray("book_details");
		JSONObject bookDetails = details.getJSONObject(0);
		title = bookDetails.getString("title");
		author = bookDetails.getString("author");
		price = bookDetails.getInt("price");
		ageGroup = bookDetails.getString("age_group");
		publisher = bookDetails.getString("publisher");
		isbn = bookDetails.getString("primary_isbn13");
	}
	
	public static void finishBook(JSONObject googleJSON, Book bk) throws JSONException {
		try{
			bk.bookURL = googleJSON.getString("selfLink");
			JSONObject volInfo = googleJSON.getJSONObject("volumeInfo");
			bk.googTitle = volInfo.getString("title");
			
			try{
				JSONArray authors = volInfo.getJSONArray("authors");
				bk.googAuths = (String)authors.get(0);
				for (int a = 1; a < authors.length(); a++){
					bk.googAuths = bk.googAuths + " : " + (String)authors.get(a);
				}
			}
			catch (Exception e) {
			}
			
			try{
			bk.googPublisher = volInfo.getString("publisher");
			}
			catch (Exception E){	
			}
			
			bk.googPublishDate = volInfo.getString("publishedDate");
			
			try{
				bk.googDescription = volInfo.getString("description");
			}
			catch (Exception e){
			}
			
			try{
				JSONArray identifiers = volInfo.getJSONArray("industryIdentifiers");
				bk.googISBN = identifiers.getString(1);
			}
			catch (Exception e) {
			}
			
			
			JSONObject readModes = volInfo.getJSONObject("readingModes");
			bk.googModeText = readModes.getBoolean("text");
			bk.googModeImage = readModes.getBoolean("image");
			bk.googPageCount = volInfo.getInt("pageCount");
			bk.googPrintType = volInfo.getString("printType");
			JSONArray cats = volInfo.getJSONArray("categories");
			bk.googCats = cats.getString(0);
			for (int c = 1; c < cats.length(); c++){
				bk.googCats = bk.googCats + " : " + cats.getString(c);
			}
			
			try{
				bk.googAvgRating = volInfo.getInt("averageRating");
				bk.googRatingCount = volInfo.getInt("ratingsCount");
				bk.googMaturity = volInfo.getString("maturityRating");
				bk.googLang = volInfo.getString("language");
			}
			catch(Exception e){
			}

			JSONObject saleInfo = googleJSON.getJSONObject("saleInfo");
			bk.googCountry = saleInfo.getString("country");
			bk.googEbook = saleInfo.getBoolean("isEbook");
			
			try{
				JSONObject listPrice = saleInfo.getJSONObject("listPrice");
				bk.googListPrice = listPrice.getDouble("amount");
				JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");
				bk.googRetailPrice = retailPrice.getDouble("amount");
			}
			catch (Exception e){
			}
			
			try{
				JSONObject accessInfo = googleJSON.getJSONObject("accessInfo");
				bk.googViewability = accessInfo.getString("viewability");
				bk.googEmbeddable = accessInfo.getBoolean("embeddable");
				bk.googPublicDomain = accessInfo.getBoolean("publicDomain");
				bk.googTextToSpeech = accessInfo.getString("textToSpeechPermission");
				bk.googAccessStatus = accessInfo.getString("accessViewStatus");
				bk.googQuoteShare = accessInfo.getBoolean("quoteSharingAllowed");
			}
			catch (Exception e){
			}

			try{
				JSONObject searchInfo = googleJSON.getJSONObject("searchInfo");
				bk.googSnippet = searchInfo.getString("textSnippet");
			}
			catch (Exception e){
			}

		}
		catch (Exception e){
			System.out.println(e);
		}
	}
	
}
