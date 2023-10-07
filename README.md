# DataRetrievalAndroidApp

This android mobile app is written in Java.
It has a button on top of the screen. When pressed, it does the following:

Retrieves the data from https://fetch-hiring.s3.amazonaws.com/hiring.json.
Displays this list of items to the user based on the following requirements:

Displays all the items grouped by "listId".
Sort the results first by "listId" then by "name" when displaying.
Filter out any items where "name" is blank or null.

The final result displayed to the user in an easy-to-read list.

All the implementation is in MainActivity.java. When the button is pressed, retrieveData() is called.
In retrieveData(), it makes a HttpURLConnection in the background, and fetches the data, which is stored as a JSON String. 
Once done with the fetch, retrieveData() calls groupByListId(), which first puts all the data in a HashMap (Map<Integer, List<Integer>>), with key as listId and value as name. Then, it groups the data by listId. 
Lastly, sortByListIDAndName() is called to sort all the names, which are the item numbers, in each group. 

All the filtered data are displayed to the user under the button in an easy-to-read list. 



