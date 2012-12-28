# Protocol - Android Networking Library
Protocol is a simple networking library for Android 2.1 and above. Protocol is built off of Android's AsyncTask and DefaultHttpClient. Below is an example of how to get a JSON from a request.

```` java
ProtocolClient client = new ProtocolClient("http://www.statuscodewhat.com");
client.doGet("/200?body={\"name1\":\"value1\"}", requestData1, new JSONResponseHandler() {

	@Override
	public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
		if (jsonArray != null) {
			Toast.makeText(getApplication(), "JSON Array Size - " + jsonArray.length(), Toast.LENGTH_SHORT).show();
		} else if (jsonObject != null) {
			Toast.makeText(getApplication(), "JSON Object Size - " + jsonObject.length(), Toast.LENGTH_SHORT).show();
		}
	}

});
		
````

## How To Get Started
- Download the Protocol JAR
- Place the JAR in the Android project's "libs" directory
- Code

## Examples

### Using Protocol at the core level - ProtocolTask
ProtocolTask is wrapped inside of ProtocolClient(see below). You probably won't have to call a ProtocolTask directly itself.
```` java
ProtocolTask task = new ProtocolTask(HttpMethod.HTTP_GET, "http://www.statuscodewhat.com/200?body=HelloWorldddd", null, new ProtocolResponseHandler() {

	@Override
	public void handleResponse(HttpResponse response, int status, byte[] data) {
		String responseData = new String(data);
		Toast.makeText(getApplication(), "ProtocolTaskResponse - " + responseData, Toast.LENGTH_SHORT).show();
	}
	
});
task.execute();
````

### Using ProtocolClient and different response types
ProtocolClient wraps ProtocolTask making common patterns easier to use:
- Set a base url for each request
- Set headers that get sent up on each request
- Set headers for individual requests
- Set a timeout for each request
- Set observers that get executed for individual status codes

```` java
// Creates a client that can get used for one to many requests.
// This client instance is setting the base url at "http://www.statuscodewhat.com"
ProtocolClient client = new ProtocolClient("http://www.statuscodewhat.com");

// Sets headers that get sent up on each request
client.addHeader("client_test_request_header", "client_test_request_header_value");
client.addHeader("client_test_request_header_override", "THIS SHOULDN'T SHOW"); // Header example further down explains why

// Performs a get request to /200?body=HelloWorld
// A null ProtocolRequestData parameter is passed (this could have been used to pass in the query param - see following doGet)
// StringResponseHandler is passed in to create a String responsenation of the response data
client.doGet("/200?body=HelloWorld", null, new StringResponseHandler() {

	@Override
	public void handleResponse(String stringResponse) {
		Toast.makeText(getApplication(), stringResponse, Toast.LENGTH_SHORT).show();
	}

	
});

// Performs a get request to /200 with ParamsRequestData to add the body parameter
// JSONResponseHandler is passed in to create a JSOON responsenation of the response data
// A JSONObject and JSONArray are passed in the response handler depending on what the resonse returns
ParamsRequestData requestData1 = new ParamsRequestData();
requestData1.addParam("body", "{\"name1\":\"value1\",\"name2\":\"value2\",\"name3\":\"value3\"}");
client.doGet("/200", requestData1, new JSONResponseHandler() {

	@Override
	public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
		if (jsonArray != null) {
			Toast.makeText(getApplication(), "JSON Array Size - " + jsonArray.length(), Toast.LENGTH_SHORT).show();
		} else if (jsonObject != null) {
			Toast.makeText(getApplication(), "JSON Object Size - " + jsonObject.length(), Toast.LENGTH_SHORT).show();
		}
	}
	
});

// Performs a get request to /200 with ParamsRequestData to add the body parameter
// JSONResponseHandler is passed in to create a JSOON responsenation of the response data
// Sets headers to send up only on this request
// NOTE: If a header set here will override a header set in the ProtocolClient
ParamsRequestData requestData3 = new ParamsRequestData();
requestData3.addHeader("test_request_header", "test_request_header_value");
requestData3.addHeader("client_test_request_header_override", "client_test_request_header_override_value");
requestData3.addParam("body", "");
requestData3.addParam("show_headers", "true");
client.doGet("/200", requestData3, new JSONResponseHandler() {

	@Override
	public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
		if (jsonObject != null) {
			try {
				JSONObject headers = jsonObject.getJSONObject("headers");
				if (headers.has("test_request_header")) {
					Toast.makeText(getApplication(), "Found test_request_header - " + headers.getString("test_request_header"), Toast.LENGTH_SHORT).show();
				}
				if (headers.has("client_test_request_header")) {
					Toast.makeText(getApplication(), "Found client_test_request_header - " + headers.getString("client_test_request_header"), Toast.LENGTH_SHORT).show();
				}
				if (headers.has("client_test_request_header_override")) {
					Toast.makeText(getApplication(), "Found client_test_request_header_override - " + headers.getString("client_test_request_header_override"), Toast.LENGTH_SHORT).show();
				}
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
});

````

### Using ProtocolClient and different request types

```` java

// Performs a post request to /200 with ParamsRequestData to add post parameters to the request body
ParamsRequestData requestData5 = new ParamsRequestData();
requestData5.addParam("first_name", "Josh");
requestData5.addParam("last_name", "Holtz");
client.doPost("/200", requestData5, new StringResponseHandler() {

	@Override
	public void handleResponse(String stringResponse) {
		if (this.getStatus() == 200) {
			Toast.makeText(getApplication(), "POST param success", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplication(), "POST params failure", Toast.LENGTH_SHORT).show();
		}
	}
	
});

// Performs a post request to /200 with JSONRequestData to add a JSON string to the request body
Map<String, String> jsonObjectData = new HashMap<String, String>();
jsonObjectData.put("first_name", "Josh");
jsonObjectData.put("last_name", "Holtz");
JSONObject jsonObject = new JSONObject(jsonObjectData);

JSONRequestData requestData6 = new JSONRequestData(jsonObject);
client.doPut("/200", requestData6, new StringResponseHandler() {

	@Override
	public void handleResponse(String stringResponse) {
		if (this.getStatus() == 200) {
			Toast.makeText(getApplication(), "POST json success", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplication(), "POST json failure", Toast.LENGTH_SHORT).show();
		}
	}
	
});

// Performs a post request to /200 with FileRequestData to add post parameters and files with to the mulit-part request body
Map<String, String> fileObjectData = new HashMap<String, String>();
fileObjectData.put("first_name", "Josh");
fileObjectData.put("last_name", "Holtz");

Map<String, File> filesData = new HashMap<String, File>();
filesData.put("file1", new File("../somepath.."));

FileRequestData requestData7 = new FileRequestData(fileObjectData, filesData);
client.doPut("/200", requestData7, new StringResponseHandler() {

	@Override
	public void handleResponse(String stringResponse) {
		if (this.getStatus() == 200) {
			Toast.makeText(getApplication(), "POST file success", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplication(), "POST file failure", Toast.LENGTH_SHORT).show();
		}
	}
	
});

````

### Observing HTTP statuses on a ProtocolClient

```` java
// This ProtocolStatusListener gets called whenver a 401 is received in a ProtocolClient
// Returning false does not allow that requests ProtocolResponseHandler (or subclass) to execute
client.observeStatus(401, new ProtocolStatusListener() {

	@Override
	public boolean observedStatus(int status, ProtocolResponseHandler handler) {
		Toast.makeText(getApplication(), "You are not logged in; We observed a status - " + status, Toast.LENGTH_SHORT).show();
		return false;
	}
	
});

// This ProtocolStatusListener gets called whenver a 500 is received in a ProtocolClient
// Returning true does allow that requests ProtocolResponseHandler (or subclass) to execute
client.observeStatus(500, new ProtocolStatusListener() {

	@Override
	public boolean observedStatus(int status, ProtocolResponseHandler handler) {
		Toast.makeText(getApplication(), "We got a server error; We observed a status - " + status, Toast.LENGTH_SHORT).show();
		return true;
	}
	
});

client.doGet("/401", null, new JSONResponseHandler() {

	@Override
	public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
		Toast.makeText(getApplication(), "The ProtocolStatusListener should catch this 401 and not show this toast", Toast.LENGTH_SHORT).show();
	}
	
});

client.doGet("/500", null, new JSONResponseHandler() {

	@Override
	public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
		Toast.makeText(getApplication(), "The ProtocolStatusListener should catch this 500 and show this toast", Toast.LENGTH_SHORT).show();
	}
	
});

````

### Subclassing ProtocolClient as a singleton

```` java
public class CustomClient extends ProtocolClient {

	private CustomClient() {
		super("http://www.statuscodewhat.com");
	}
	
	public static CustomClient getInstance() {
		return LazyHolder.instance;
	}
	
	private static class LazyHolder {
		private static CustomClient instance = new CustomClient();
	}
	
	/**
	  * This wrapper of doGet is pointless but just an example of what a subclassed ProtocolClient could do
	  */
	public static ProtocolTask get(String route, ParamsRequestData requestData, JSONResponseHandler responseHandler) {
		return CustomClient.getInstance().doGet(route, requestData, responseHandler);
	}
	
	/**
	  * This wrapper of doPost is pointless but just an example of what a subclassed ProtocolClient could do
	  */
	public static ProtocolTask post(String route, JSONRequestData requestData, JSONResponseHandler responseHandler) {
		return CustomClient.getInstance().doPost(route, requestData, responseHandler);
	}
	
	/**
	  * This wrapper of doPut is pointless but just an example of what a subclassed ProtocolClient could do
	  */
	public static ProtocolTask put(String route, JSONRequestData requestData, JSONResponseHandler responseHandler) {
		return CustomClient.getInstance().doPut(route, requestData, responseHandler);
	}
	
	/**
	  * This wrapper of doDelete is pointless but just an example of what a subclassed ProtocolClient could do
	  */
	public static ProtocolTask delete(String route, ParamsRequestData requestData, JSONResponseHandler responseHandler) {
		return CustomClient.getInstance().doDelete(route, requestData, responseHandler);
	}
	
}

````
