package getData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class Book {

	public String isbn;
	public String listDate;
	public int highestRank;
	public int mostWeeksOnList;
	public String title;
	public String author;
	public int price;
	public String ageGroup;
	public String publisher;

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

}
