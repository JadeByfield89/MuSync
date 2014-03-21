
package com.jbsoft.musync.providers;

import com.jbsoft.musync.parsers.JSONParser;
import com.jbsoft.musync.utilities.HttpUrlConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class ArtistImageProvider {

    private String query;

    private static final String API_KEY = "3R1H8G93COCKZRNXV";

    private static final String TAG = "ArtistImageProvider";

    private URI uri;

    private String link;

    private URL url;

    private static ArrayList<String> mLinks = new ArrayList<String>();

    public ArtistImageProvider(String artistName) {

        this.query = artistName;

    }

    public String getArtistPhotoUrl() {

        String string_url = "http://developer.echonest.com/api/v4/artist/images?api_key=" + API_KEY
                + "&name=" + query + "&license=cc-by-sa";

        // First formate the url to accept any format/paramaters

        try {
            url = new URL(string_url);
            uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
                    url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {

            Log.d("GoogleImageProvider", "URL IS " + string_url);

            final JSONParser jsonParser = new JSONParser();
            HttpUrlConnection mConnection = new HttpUrlConnection();

            String response = mConnection.sslhttpgetdata(url.toString());

            if (response != null) {
                JSONObject jObject = jsonParser.getJSONFromString(response);

                JSONObject object = jObject.getJSONObject("response");
                JSONArray array = object.getJSONArray("images");
                Log.d(TAG, "Response is " + object.toString());

                JSONObject first = array.getJSONObject(0);
                Log.d(TAG, "Images[0] is " + first.toString());
                link = first.getString("url");
                mLinks.add(link);
                Log.d(TAG, "URL IS " + link);
            } else {
                Log.d(TAG, "Response returned NULL");
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        Log.d(TAG, "Link is " + link);
        return link;

    }

    public static ArrayList<String> getLinks() {

        return mLinks;
    }
}
