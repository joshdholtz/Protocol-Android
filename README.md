Protocol is a simple networking library for Android 2.1 and above. Protocol is built off of Android's AsyncTask and DefaultHttpClient. Below is an example of how to get a JSON from a request.

```` java
ProtocolClient client = new ProtocolClient("http://www.statuscodewhat.com");
client.doGet("/200", requestData1, JSONResponse.class, new ProtocolResponseHandler<JSONResponse>() {

	@Override
	public void handleResponse(JSONResponse responseData) {
		if (responseData.getJsonArray() != null) {
			Toast.makeText(getApplication(), "JSON Array Size - " + responseData.getJsonArray().length(), Toast.LENGTH_SHORT).show();
		} else if (responseData.getJsonObject() != null) {
			Toast.makeText(getApplication(), "JSON Object Size - " + responseData.getJsonObject().length(), Toast.LENGTH_SHORT).show();
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
ProtocolTask task = new ProtocolTask(HttpMethod.HTTP_GET, "http://www.statuscodewhat.com/200?body=HelloWorld", null, new ProtocolGotResponse() {

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
client.addHeader("client_test_request_header_override", "THIS SHOULDN'T SHOW");

// Performs a get request to /200?body=HelloWorld
// A null ProtocolRequestData parameter is passed (this could have been used to pass in the query param - see following doGet)
// StringResponse.class and ProtocolResponseHandler<StringResponse> are passed in to create a String response handler
// Third parameter class and fourth parameter generic class must match
client.doGet("/200?body=HelloWorld", null, StringResponse.class, new ProtocolResponseHandler<StringResponse>() {

	@Override
		public void handleResponse(StringResponse responseData) {
		Toast.makeText(getApplication(), responseData.getString(), Toast.LENGTH_SHORT).show();
	}

});

// Performs a get request to /200 with ParamsRequestData to add the body parameter
// JSONResponse.class and ProtocolResponseHandler<JSONResponse> are passed in to create a JSON response handler
// A JSONResponse has a getJsonArray() and getJsonObject() method (depending on what the resonse returns)
ParamsRequestData requestData1 = new ParamsRequestData();
requestData1.addParam("body", "{\"name1\":\"value1\",\"name2\":\"value2\",\"name3\":\"value3\"}");
client.doGet("/200", requestData1, JSONResponse.class, new ProtocolResponseHandler<JSONResponse>() {

	@Override
	public void handleResponse(JSONResponse responseData) {
		if (responseData.getJsonArray() != null) {
			Toast.makeText(getApplication(), "JSON Array Size - " + responseData.getJsonArray().length(), Toast.LENGTH_SHORT).show();
		} else if (responseData.getJsonObject() != null) {
			Toast.makeText(getApplication(), "JSON Object Size - " + responseData.getJsonObject().length(), Toast.LENGTH_SHORT).show();
		}
	}

});

// Performs a get request to /200 with ParamsRequestData to add the body parameter
// JSONResponse.class and ProtocolResponseHandler<JSONResponse> are passed in to create a JSON response handler
// Sets headers to send up only on this request
// NOTE: If a header set here will override a header set in the ProtocolClient
ParamsRequestData requestData3 = new ParamsRequestData();
requestData3.addHeader("test_request_header", "test_request_header_value");
requestData3.addHeader("client_test_request_header_override", "client_test_request_header_override_value");
requestData3.addParam("body", "");
requestData3.addParam("show_headers", "true");
client.doGet("/200", requestData3, JSONResponse.class, new ProtocolResponseHandler<JSONResponse>() {

	@Override
	public void handleResponse(JSONResponse responseData) {
		if (responseData.getJsonObject() != null) {
			try {
				JSONObject headers = responseData.getJsonObject().getJSONObject("headers");
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
