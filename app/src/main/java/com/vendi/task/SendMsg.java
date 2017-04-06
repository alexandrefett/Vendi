package com.vendi.task;

import android.os.AsyncTask;
import android.util.Log;

import com.vendi.Api;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Alexandre on 17/08/2016.
 */
public class SendMsg extends AsyncTask<Void, Void, Void> {
    private String token="";
    private String title="";
    private String body="";

    public SendMsg(String t, String title, String body){
        this.token = t;
        this.title = title;
        this.body = body;
    }

    protected Void doInBackground(Void... params) {
        try {
            String not_key = Api.sendMSG(token,title,body);
            Log.d("--->>>","task return:"+not_key);
        } catch (JSONException e) {
            Log.d("--->>>","task Jsonexception:"+e.getMessage());
        } catch (IOException e) {
            Log.d("--->>>","task ioexception:"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute() {

    }
}