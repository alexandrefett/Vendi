package com.vendi;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Api {
    public static final String senderId="509197457426";
    public static final String key="AIzaSyCuOb4nvBRaR9WqBYRFUwxisuFVOLijEjg";
    public static final String  TAG = "--->>>";

    public static String sendMSG(String not_key, String title, String text) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("to", not_key);
        body.put("priority", "high");

        JSONObject notification = new JSONObject();
        notification.put("body", text);
        notification.put("title", title);

        body.put("notification", notification);

        URL url = new URL("https://fcm.googleapis.com/fcm/send");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);

        con.setRequestProperty("Authorization", "key="+key);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");
        con.connect();

        OutputStream os = con.getOutputStream();
        os.write(body.toString().getBytes("UTF-8"));
        os.close();

        InputStream is = con.getInputStream();
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        is.close();

        JSONObject response = new JSONObject(responseString);
        Log.d("--->>>","Response: "+ response.toString());
        return response.toString();
    }
}
