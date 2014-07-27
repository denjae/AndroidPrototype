package com.example.denjae.androidprototype;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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

import org.json.JSONObject;


public class MainActivity extends Activity implements View.OnClickListener {

    ProgressBar progressBar;
    Button sendButton;
    TextView threat;
    EditText cityInput;
    EditText plzInput;
    int plz;
    String location;
    int foursquare;
    String foursquareString;


//onCreate-Methode. Interaktionselemente werden initialisiert, ein Klick-Listener auf den Senden-Button gesetzt und der ProgressBar-Balken versteckt
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findVIewsById();
        progressBar.setVisibility(View.INVISIBLE);
        sendButton.setOnClickListener(this);

    }
//    Initialisiert die Interaktionselemente
    private void findVIewsById() {
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.sendButton = (Button) findViewById(R.id.sendLocation);
        this.threat = (TextView) findViewById(R.id.threatLevelOutput);
        this.cityInput = (EditText) findViewById(R.id.cityInput);
        this.plzInput = (EditText) findViewById(R.id.plzInput);
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
        plz = Integer.parseInt(plzInput.getText().toString());
        }
        catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setMessage("error").setNeutralButton("test", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
        location = cityInput.getText().toString();
        Log.w("debug", "location: " + location + ", plz:" + plz);
    }

//Ruft die Ergebnisse der eingegebenen Stadt von Foursquare ab und gibt diese als String zurueck
    public String getFoursquareData(String location){
        this.location=location;
        String venueURL= "https://api.foursquare.com/v2/venues/search?near="+location+"&client_id=BXBK3ZES42YG5KDEBCCFCOKZTYKZIP1LYZYXCJCGNO2ORTB5&client_secret=KE53YHPKFWUS4LJ5JLU1EFOKUPPDBFDFZWZINVBK0QMHIATA&v=20140726";

        return foursquareString;
    }


    //Aus der Ermittlung der einzelnen Quellen wird in dieser Funktion der Bedrohungsgrad entwickelt
    public int threatLevel(){
        int level = 0;

        return level;
    }

}
