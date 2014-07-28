package com.example.denjae.androidprototype;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class MainActivity extends Activity implements View.OnClickListener {

    ProgressBar progressBar;
    Button sendButton;
    TextView threat;
    EditText cityInput;
    EditText plzInput;
    String location;
    int foursqareLevel;


//onCreate-Methode. Interaktionselemente werden initialisiert, ein Klick-Listener auf den Senden-Button gesetzt und der ProgressBar-Balken versteckt
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findVIewsById();
        sendButton.setOnClickListener(this);

    }
//    Initialisiert die Interaktionselemente
    private void findVIewsById() {
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.sendButton = (Button) findViewById(R.id.sendLocation);
        this.threat = (TextView) findViewById(R.id.threatLevelOutput);
        this.cityInput = (EditText) findViewById(R.id.cityInput);
    }

    //Von IDE erstellt
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //Von IDE erstellt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Speichern der eingegebenen Daten beim Klicken des Senden-Buttons.
    //Fehlermeldung, falls eine ungueltige Postleitzahl eingegegben wird
    @Override
    public void onClick(View view) {
        try{
        location = cityInput.getText().toString();
        Log.w("debug", "location: " + location);
        getJSONFromForsquare(location);
        }
        catch (Exception e) {
            new AlertDialog.Builder(this).setMessage("Fehler bei der Verarbeitung der Daten").setNeutralButton("Erneut versuchen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();

        }


    }

//Ruft die Ergebnisse der eingegebenen Stadt von Foursquare ab und wertet diese in Form eines Integers aus
    public int getJSONFromForsquare(String location) {
        this.location = location;
        String urlFoursquare = "https://api.foursquare.com/v2/venues/search?near=" + location + "&client_id=BXBK3ZES42YG5KDEBCCFCOKZTYKZIP1LYZYXCJCGNO2ORTB5&client_secret=KE53YHPKFWUS4LJ5JLU1EFOKUPPDBFDFZWZINVBK0QMHIATA&v=20140726";
        new AsyncRequest(progressBar).execute(urlFoursquare);
        return foursqareLevel;
    }


    //Aus der Ermittlung der einzelnen Quellen wird in dieser Funktion der Bedrohungsgrad entwickelt
    public int threatLevel(){
        int level = 0;

        return level;
    }

}
