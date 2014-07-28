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
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by denjae on 28.07.14.
 */
public class AsyncRequest extends AsyncTask<String, String, String> {
    ProgressBar progressBar;

    public AsyncRequest(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }


    @Override
    protected String doInBackground(String... url) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        Log.w("ba", "URL: " + url[0]);
        try {
            response = httpclient.execute(new HttpGet(url[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        Log.w("ba", "Response: " + responseString);
        return responseString;

    }

    @Override
    protected void onPreExecute() {
        this.progressBar.setVisibility(View.VISIBLE);
        Log.w("ba", "onPreExecute called");
    }

    @Override
    protected void onPostExecute(String result) {
        this.progressBar.setVisibility(View.INVISIBLE);
        Log.w("ba", "onPostExecute called");
    }
}
