package getData;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONException;
import org.json.JSONObject;

public class getBooks {
	public static void main(String[] args) throws Exception {

			getBooks bestsellers = new getBooks();

			String url = "http://api.nytimes.com/svc/books/v2/lists.json?list=hardcover-fiction&date=2015-11-08&api-key=" + keyHolder.key1;
			bestsellers.sendGet(url);
			
			//making and formatting dates
			SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
			Date startDate = ft.parse("2015-01-01");
			Date endDate = ft.parse("2015-06-01");
			System.out.println(ft.format(startDate));
			System.out.println(ft.format(endDate));
			//adding a week to a date
			Date nextWeek = nextWeek(startDate);
			System.out.println(ft.format(nextWeek));
			//comparing dates
			if(startDate.compareTo(endDate) < 0){
				System.out.println("startDate is before endDate");
			}
			

	}
	private void sendGet(String url) throws Exception {
		
		URL asURL = new URL(url);
		HttpURLConnection con = (HttpURLConnection) asURL.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request");
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		JSONObject json = new JSONObject(response.toString());
		System.out.println(json.get("num_results"));

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
