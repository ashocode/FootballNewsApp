package com.example.android.footballnews;

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


public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String KEY_UNKNOWN = "unknown";
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_WEB = "webTitle";
    private static final String KEY_SECTION = "sectionName";
    private static final String KEY_DATE = "webPublicationDate";
    private static final String KEY_URL = "webUrl";
    private static final String KEY_TAGS = "tags";
    private static final int KEY_CONTRIBUTOR_OBJECT = 0;
    private static final String KEY_AUTHOR = "webTitle";
    private static final String ERROR_URL = "Error while creating request URL.";
    private static final String KEY_CHARSET = "UTF-8";
    private static final String KEY_REQUEST_TYPE = "GET";
    private static final String ERROR_RESPONSE_CODE = "Error response code: ";
    private static final String ERROR_RETRIEVE_JSON = "Problem retrieving the news JSON results.";
    private static final String ERROR_PARSE_JSON = "Problem parsing the news JSON results";
    private static final String ERROR_CLOSE_STREAM = "Error closing input stream";
    private static final int READ_TIMEOUT = 10000 /* milliseconds */;
    private static final int CONNECTION_TIMEOUT = 15000 /* milliseconds */;


    QueryUtils() {
    }

    private static URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, ERROR_URL, e);
        }
        return url;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(KEY_CHARSET));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setRequestMethod(KEY_REQUEST_TYPE);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, ERROR_RESPONSE_CODE + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, ERROR_RETRIEVE_JSON, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static List<NewsItem> extractNews(String newsJSON) {
        char letterT = 'T';
        char letterZ = 'Z';
        char space = ' ';
        String author = KEY_UNKNOWN;

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        List<NewsItem> news = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(newsJSON);
            JSONObject response = jsonResponse.getJSONObject(KEY_RESPONSE);
            JSONArray result = response.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < result.length(); i++) {
                JSONObject properties = result.getJSONObject(i);
                String title = properties.getString(KEY_WEB);
                String section = properties.getString(KEY_SECTION);
                String date = properties.getString(KEY_DATE);
                String correctDate = date.replace(letterT, space).replace(letterZ, space);
                String urlString = properties.getString(KEY_URL);
                JSONArray tags = properties.getJSONArray(KEY_TAGS);
                if ((tags != null)&&(tags.length()>0)){
                    JSONObject contributorData = tags.getJSONObject(KEY_CONTRIBUTOR_OBJECT);
                    author = contributorData.getString(KEY_AUTHOR);
                }

                NewsItem item = new NewsItemData(title, section, correctDate, urlString, author);
                news.add(item);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, ERROR_PARSE_JSON, e);
        }

        return news;
    }

    public static List<NewsItem> fetchNews(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, ERROR_CLOSE_STREAM, e);
        }

        List<NewsItem> news = extractNews(jsonResponse);
        return news;
    }
}
