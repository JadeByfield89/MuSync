
package com.jbsoft.musync.utilities;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlConnection {

    String str_url;

    Context context;

    String str_response;

    public HttpUrlConnection() {

    }

    public String sslhttpgetdata(String url) {
        return sslHttpget(url);
    }

    private String sslHttpget(String url) {
        try {

            URL urlpost = new URL(url);

            HttpURLConnection httpURLConnection = (HttpURLConnection)urlpost.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "UTF-8");

            str_response = streamToString(httpURLConnection.getInputStream());

        } catch (Exception e) {

            e.printStackTrace();
        }
        return str_response;
    }

    public String streamToString(InputStream is) throws IOException {
        String string = "";

        if (is != null) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            string = stringBuilder.toString();
        }

        return string;
    }
}
