package com.example.denjae.androidprototype;


import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by denjae on 28.07.14.
 */
public class AsyncRequest extends AsyncTask<String, String, JSONObject> {
    ProgressBar progressBar;

    public AsyncRequest(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    //Führt die asynchrone Abfrage an den jeweiligen Dienstanbieter aus. Die Abfrage wird via GET im REST-Stil durchgeführt.
    protected JSONObject doInBackground(String... url) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        JSONObject responseJson = null;
        try {
            response = httpclient.execute(new HttpGet(url[0]));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                try {
                    responseJson = new JSONObject(out.toString());
                } catch (JSONException e) {
                    Log.d("debug", "ERROR parsing the json object");
                }
            } else {
                //Verbindung schließen
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.d("debug", "ERROR receiving HTTP");
        } catch (IOException e) {
            Log.d("debug", "ERROR receiving HTTP");
        }
        return responseJson;

    }

    //Vor Beginn der Abfrage wird der Ladebalken erstellt
    @Override
    protected void onPreExecute() {
        this.progressBar.setVisibility(View.VISIBLE);
        Log.d("debug", "onPreExecute called");
    }

    //Wenn die Abfrage abgeschlossen ist, wird der Ladebalken entfernt
    @Override
    protected void onPostExecute(JSONObject resultJson) {
        this.progressBar.setVisibility(View.INVISIBLE);
        Log.d("debug", "onPostExecute called");

    }
}
