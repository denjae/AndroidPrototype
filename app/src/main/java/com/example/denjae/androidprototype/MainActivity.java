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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity implements View.OnClickListener {

    ProgressBar progressBar;
    Button sendButton;
    TextView threat;
    EditText cityInput;
    int foursqareLevel;
    AsyncRequest asyncRequest;
    JSONObject json;
    JSONObject jsonStats;

    //onCreate-Methode. Interaktionselemente werden initialisiert, ein Klick-Listener auf den Senden-Button gesetzt und der ProgressBar-Balken versteckt
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findVIewsById();
        sendButton.setOnClickListener(this);

        asyncRequest = new AsyncRequest(progressBar);


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
    //Fehlermeldung, falls ein Fehler auftritt
    @Override
    public void onClick(View view) {
        try {
            threatFoursqure(cityInput.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(this).setMessage("Fehler bei der Verarbeitung der Daten").setNeutralButton("Erneut versuchen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();

        }


    }


    public int threatFoursqure(String location) throws ExecutionException, InterruptedException, JSONException {
        foursqareLevel=0;
        String urlFoursquare = "https://api.foursquare.com/v2/venues/search?near=" + location + "&limit=3&novelty=new&client_id=BXBK3ZES42YG5KDEBCCFCOKZTYKZIP1LYZYXCJCGNO2ORTB5&client_secret=KE53YHPKFWUS4LJ5JLU1EFOKUPPDBFDFZWZINVBK0QMHIATA&v=20140726";
        json = new JSONObject(asyncRequest.execute(urlFoursquare).get());
        for (int i = 0; i < json.length(); i++) {
            Log.d("debug", "Loop stated");
            Log.d("debug", "Created json object" + json);
            foursqareLevel+= json.getInt("tipCount");

           // foursqareLevel += json.getJSONObject("stats").getInt("tipCount");
           // foursqareLevel += json.getJSONObject("stats").getInt("usersCount");
        }

        Log.d("debug", "Ermitteltes Level Foursquare " + foursqareLevel);

        return foursqareLevel;
    }

}