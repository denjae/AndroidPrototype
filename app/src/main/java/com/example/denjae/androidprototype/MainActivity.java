package com.example.denjae.androidprototype;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity implements View.OnClickListener {

    ProgressBar progressBar;
    Button sendButton;
    Button recreate;
    TextView threatLevelOutput;
    EditText cityInput;
    int foursqareLevel;
    AsyncRequest asyncRequestFourSquare;
    AsyncRequest asyncRequestWlan;
    JSONObject json;
    JSONObject jsonWlan;
    JSONArray jsonArray;
    int wlanLevel;
    double lat;
    double lon;
    int foursquare;
    int wlan;
    JSONObject tmp;
    int result;
    int resultFoursquare;


    //onCreate-Methode. Interaktionselemente werden initialisiert, ein onClick-Listener auf den Senden-Button gesetzt und der ProgressBar-Balken versteckt
    //Alternativ wird für den restart-Button die Applikation aktualisiert
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findVIewsById();
        sendButton.setOnClickListener(this);
        recreate.setOnClickListener(this);
        asyncRequestFourSquare = new AsyncRequest(progressBar);
        asyncRequestWlan = new AsyncRequest(progressBar);
    }


    //    Initialisiert die Interaktionselemente
    private void findVIewsById() {
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.sendButton = (Button) findViewById(R.id.sendLocation);
        this.threatLevelOutput = (TextView) findViewById(R.id.threatLevelOutput);
        this.cityInput = (EditText) findViewById(R.id.cityInput);
        this.recreate = (Button) findViewById(R.id.recreate);
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
        if (view == sendButton) {
            try {
                //Fuehrt die Methode zur Berechnung des Bedrhohungsgrades aus
                threatLevel(cityInput.getText().toString());
                //Versteckt die Tastatur nach Absenden der Anfrage
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                //Meldung im Fehlerfall
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
        //Aktualisiert die Anwendung
        if (view == recreate) {
            cityInput.setText("");
            recreate();
        }
    }

    //Es werden die ermittelten Werte zusammengetragen und daraus der Bedrohungsgrad bestimmt. Die if-Anweisungen bestimmen Bedrohungsgrad und die Färbung der Ausgabebox
    public void threatLevel(String location) throws InterruptedException, ExecutionException, JSONException, IOException {
        //Speichert den Rueckgabewert der Methode von Foursquare
        foursquare = threatFoursqure(location);
        //Speichert den Rueckgabewert der Methode von OpenSignal
        wlan = threatSignal(location);
        //Addiert die Rueckgabewerte
        result = wlan + foursquare;

        //Bestimmt den Bedrohungsgrad, resultierend auf der Summe der Einzelbedrohungen. Setzt Hintergrundfarbe und Text des Ausgabefensters
        if (result <= 3) {
            threatLevelOutput.setText("Geringer Bedrohungsgrad");
            threatLevelOutput.setBackgroundColor(Color.GREEN);
        } else if (result == 4) {
            threatLevelOutput.setText("Mittlerer Bedrohungsgrad");
            threatLevelOutput.setBackgroundColor(Color.YELLOW);
        } else {
            threatLevelOutput.setText("Hoher Bedrohungsgrad");
            threatLevelOutput.setBackgroundColor(Color.RED);
        }
    }

    //Fragt die Werte ab, die über den Ort von Foursquare bestimmt werden
    private int threatFoursqure(String location) throws ExecutionException, InterruptedException, JSONException {
        foursqareLevel = 0;
        String urlFoursquare = "https://api.foursquare.com/v2/venues/search?near=" + location + "&&novelty=new&client_id=BXBK3ZES42YG5KDEBCCFCOKZTYKZIP1LYZYXCJCGNO2ORTB5&client_secret=KE53YHPKFWUS4LJ5JLU1EFOKUPPDBFDFZWZINVBK0QMHIATA&v=20140726";
        json = new JSONObject();
        json = asyncRequestFourSquare.execute(urlFoursquare).get();

        jsonArray = new JSONArray();
        jsonArray = json.getJSONObject("response").getJSONArray("venues");
        //Werte aus dem json-Objekt abfragen
        for (int i = 0; i < jsonArray.length(); i++) {
            foursqareLevel += jsonArray.getJSONObject(i).getJSONObject("stats").getInt("checkinsCount");
            foursqareLevel += jsonArray.getJSONObject(i).getJSONObject("stats").getInt("tipCount");
            foursqareLevel += jsonArray.getJSONObject(i).getJSONObject("stats").getInt("usersCount");
        }
        //Setzen des Rueckgabewertes, in Abhaengigkeit des ermittelten Wertes
        if (foursqareLevel <= 2500) {
            resultFoursquare = 1;
        } else if (foursqareLevel > 2500 && foursqareLevel <= 12500) {
            resultFoursquare = 2;
        } else {
            resultFoursquare = 3;
        }
        return resultFoursquare;
    }

    //Bestimmt anhand der Informationen von OpenSignal einen Teil des Bedrohungsgrades
    public int threatSignal(String location) throws IOException, ExecutionException, InterruptedException {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        wlanLevel = 0;
        //Wandelt den Ort in Laengen- und Breitengrad
        try {
            address = coder.getFromLocationName(location, 5);
            if (address == null) {
                return 0;
            }
            Address adress = address.get(0);
            lat = adress.getLatitude();
            lon = adress.getLongitude();

        } catch (Exception e) {

        }
        //String zur Abfrage der Werte
        String urlWlan = "http://api.opensignal.com/v2/networkrank.json?lat=" + lat + "&lng=" + lon + "&distance=5&apikey=e65336073b1a9853cd5755e92dba465c";

        try {
            jsonWlan = new JSONObject();
            jsonWlan = asyncRequestWlan.execute(urlWlan).get();
        } catch (Exception e) {
        }

        try {
            tmp = jsonWlan.getJSONObject("networkRank");
        } catch (JSONException e) {
        }
        //Setzt Rueckgabewert. Bei Verfuegbarkeit aller Netze ist dieser 1 (+1 wegen Datenintegritaet)
        if (tmp.length() == 4) {
            wlanLevel = 2;
        } else if (tmp.length() == 3) {
            wlanLevel = 3;
        } else {
            wlanLevel = 4;
        }
        return wlanLevel;
    }
}


