package getData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class getGoogleData {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		getGoogleData googleBooks = new getGoogleData();
		
		// API request format, Google Bestsellers:
		// GET https://www.googleapis.com/books/v1/volumes?q=TITLEKEYWORD+isbn=ISBN&key=KEY
		// replace KEY with API key
		// replace TITLEKEYWORD with title keyword
		// replace ISBN with an isbn number
		String title = "THE GIRL ON THE TRAIN";
		String ISBN = "9781594633669";
		String publisher = "Riverhead";
		String url = "https://www.googleapis.com/books/v1/volumes?q=" + title + "+intitle:" + title + "+inpublisher:" + publisher + "+isbn:" + ISBN + "&key=" + keyHolder.key2;
		googleBooks.sendGet(url);
		
	}
	
	private JSONObject sendGet(String url) throws Exception {
		
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
		
		return json;
	}

}
