
package com.jbsoft.musync.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import java.io.InputStream;

public class JSONParser {

    static InputStream is = null;

    static JSONObject jObj = null;

    static String json = "";

    // constructor
    public JSONParser() {

    }

    public JSONObject getJSONFromString(String str_data) {

        // try parsing the string to a JSON object
        try {
            if (json != null) {
                jObj = new JSONObject(str_data);
                Log.d("JSONParser", jObj.toString());
            } else {
                jObj = null;
            }
        } catch (JSONException e) {
            Log.d("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }
}
