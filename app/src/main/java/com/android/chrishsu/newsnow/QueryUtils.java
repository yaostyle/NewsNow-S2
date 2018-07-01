package com.android.chrishsu.newsnow;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

// Create a query utility
public class QueryUtils {
    // Create current LOG_TAG name
    public static final String LOG_TAG = NewsActivity.class.getName();

    // Declare const vars
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final String HTTP_METHOD = "GET";
    private static final String KEY_TITLE = "webTitle";
    private static final String KEY_SECTION = "sectionName";
    private static final String KEY_PUB_DATE = "webPublicationDate";
    private static final String KEY_WEB_URL = "webUrl";
    private static final String KEY_TAGS = "tags";

    // Create an empty QueryUtils constuctor
    private QueryUtils() {
        //empty constructor
    }

    // Create a fetchData method with url params
    public static List<News> fetchData(String requestUrl) {
        // Pause the app for 2.5sec to mimic the slow connection loading state
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create a url object with url param
        URL url = createUrl(requestUrl);

        // Create a set an empty json response object
        String jsonResponse = null;

        // Try to call makeHttpRequest method with the supplied url
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // If there's any error, catch with IOException with detail response assigned to 'e'
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Create a news list and get json's data with extractFeatureFromJson method
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list
        return news;
    }

    // Create a function to create url object
    private static URL createUrl(String stringUrl) {
        // Create and set an empty url object
        URL url = null;

        // Try to create the url from the supplied string
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            // If the url is not a valid url format,
            // catch with MalformedURLException with response assigned to 'e'
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        // Return the url object
        return url;
    }

    // Create makeHttpRequest that checks IOException
    private static String makeHttpRequest(URL url) throws IOException {
        // Create an empty json response var
        String jsonResponse = "";

        // If url is empty, return empty var first
        if (url == null) {
            return jsonResponse;
        }

        // Init empty connection & input stream vars
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        // Try method to setup and open a http connection
        // with read timeout of 10sec and connection timeout of 15sec
        // The connection is set to use "GET" http method
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod(HTTP_METHOD);
            urlConnection.connect();

            // If the connection responded with code 200 (successful)
            // get the input stream and read it
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                // Otherwise, log the error
                Log.e(LOG_TAG, "Error reponse code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            // If the connection has problem..
            // Catch IOException detail with assigned 'e'
            Log.e(LOG_TAG, "Problem retrieving the JSON: ", e);
        } finally {
            // Finally, if connection is successfully established,
            // disconnect and close both the connection and input stream
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        // Return the json response object
        return jsonResponse;
    }

    // Create readFromStream that checks IOException error
    private static String readFromStream(InputStream inputStream) throws IOException {
        // Create a StringBuilder for output data
        StringBuilder output = new StringBuilder();
        // If inputStream has data, run the following
        if (inputStream != null) {
            // Create a InputStreamReader to read from the inputStream
            // Also, set its encoding to UTF-8
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                                                                        Charset.forName("UTF-8"));

            // Create a BufferredReader to read from the inputStream
            BufferedReader reader = new BufferedReader(inputStreamReader);
            // Create a var from reading each line
            String line = reader.readLine();
            // Keep adding each line to the output
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }

        }
        // Return the output (json) data
        return output.toString();
    }

    // Create extractFeatureFromJson to extract json's object properties
    public static List<News> extractFeatureFromJson(String newsJSON) {
        // Checks if json is not empty, otherwise return null
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create a news ArrayList
        List<News> news = new ArrayList<>();

        // Try block to get the json object & arrays
        try {
            // Createa a base json response object
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            // Create a json object from "response"
            JSONObject baseJsonResponseResult = baseJsonResponse.getJSONObject("response");
            // Creat a json array object from "result"
            JSONArray newsArray = baseJsonResponseResult.getJSONArray("results");

            // Iterate the array list
            for (int i = 0; i < newsArray.length(); i++) {
                // Create a single news json object
                JSONObject currentNews = newsArray.getJSONObject(i);
                // Get the current json object with the following properties:
                // title, section, published date and url

                String title = currentNews.getString(KEY_TITLE);
                String section = currentNews.getString(KEY_SECTION);
                String date = formatDate(currentNews.getString(KEY_PUB_DATE));
                String weburl = currentNews.getString(KEY_WEB_URL);

                // Declar an empty string for author in case there's no value
                String author = "";
                // Check if array object "references" has any value
                if (currentNews.getJSONArray(KEY_TAGS).length() > 0) {
                    // If so, extract its (author) value
                    author = currentNews
                        .getJSONArray(KEY_TAGS)
                        .getJSONObject(0)
                        .getString(KEY_TITLE);
                }

                // Create a new News and append to newsfeed
                News newsfeed = new News(title, section, date, weburl, author);
                news.add(newsfeed);
            }

        } catch (JSONException e) {
            // If there's JSONException, log it
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }
        // Return the news list
        return news;
    }

    // Create a formatDate
    private static String formatDate(String date){
        // Get ride of time zone info and return only the date format
        return date.substring(0, date.indexOf("T"));
    }
}
