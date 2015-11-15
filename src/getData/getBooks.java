package getData;

public class getBooks {
	// API request format, NYT Bestsellers:
	// GET http://api.nytimes.com/svc/books/v2/lists.json?list=hardcover-fiction&date=2015-11-08&api-key=KEY
	// replace KEY with API key
	// set date=_____  desired date of list in form YYYY-MM-DD
	
	// Q: Do I need to get the history of the books on the list separately?
	// (Total number of weeks on list, etc.)
	// MAINLY: Does 'weeks on list' mean in total, or in a row???
	
	
	// API request format, Google Bestsellers:
	// GET https://www.googleapis.com/books/v1/volumes?q=TITLEKEYWORD+isbn=ISBN:keyes&key=KEY
	// replace KEY with API key
	// replace TITLEKEYWORD with title keyword
	// replace ISBN with an isbn number
	
	// both api's will give results as a JSON object
}
