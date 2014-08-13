package com.example.denjae.androidprototype;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
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
    AsyncRequest asyncRequest;
    JSONObject json;
    JSONObject jsonWlan;
    JSONArray jsonArray;
    int wlanLevel;
    double lat;
    double lon;
    int foursquare;
    int wlan;
    JSONObject tmp;


    //onCreate-Methode. Interaktionselemente werden initialisiert, ein onClick-Listener auf den Senden-Button gesetzt und der ProgressBar-Balken versteckt
    //Alternativ wird für den restart-Button die Applikation aktualisiert
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findVIewsById();
        sendButton.setOnClickListener(this);
        recreate.setOnClickListener(this);
        asyncRequest = new AsyncRequest(progressBar);


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
    public void threatLevel(String location) throws ExecutionException, InterruptedException, JSONException, IOException {
        foursquare = threatFoursqure(location);
        wlan = threatWlan(location);
        try {
            int wlan = threatWlan(location);
        } catch (IOException e) {
            Log.d("debug", "Fehler threatWlan");
        }

        if (foursquare <= 9300) {
            threatLevelOutput.setText("Geringer Bedrohungsgrad");
            threatLevelOutput.setBackgroundColor(Color.GREEN);
        } else if (foursquare > 9300 && foursquare <= 18000) {
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
        json = asyncRequest.execute(urlFoursquare).get();

        jsonArray = new JSONArray();
        jsonArray = json.getJSONObject("response").getJSONArray("venues");
        //Werte aus dem json-Objekt abfragen
        Log.d("debug", "Created json Array" + jsonArray);
        for (int i = 0; i < jsonArray.length(); i++) {
            foursqareLevel += jsonArray.getJSONObject(i).getJSONObject("stats").getInt("checkinsCount");
            foursqareLevel += jsonArray.getJSONObject(i).getJSONObject("stats").getInt("tipCount");
            foursqareLevel += jsonArray.getJSONObject(i).getJSONObject("stats").getInt("usersCount");
        }
        Log.d("debug", "Created json stats" + jsonArray);
        Log.d("debug", "Ermitteltes Level Foursquare " + foursqareLevel);

        return foursqareLevel;

    }

    public int threatWlan(String location) throws IOException, ExecutionException, InterruptedException {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        wlanLevel = 0;

        try {
            address = coder.getFromLocationName(location, 5);
            if (address == null) {
                return 0;
            }
            Address adress = address.get(0);
            lat =  adress.getLatitude();
            lon =  adress.getLongitude();

        } catch (Exception e) {
            Log.d("debug", "Fehler beim Erhalt lat und lon");
        }
        String urlWlan = "http://api.opensignal.com/v2/networkrank.json?lat="+lat+"&lng="+lon+"&distance=10&apikey=6dd08f8a479ec50a0f5b5eb14fd7736e";

        String testJson = "{\n" +
                "    \"apiVersion\": \"2\",\n" +
                "    \"distance\": \"5\",\n" +
                "    \"latitude\": \"51.02667\",\n" +
                "    \"longitude\": \"7.56928\",\n" +
                "    \"networkRank\": {\n" +
                "        \"network26202\": {\n" +
                "            \"type2G\": {\n" +
                "                \"averageRssiAsu\": \"9.351622\",\n" +
                "                \"averageRssiDb\": \"-94.296757\",\n" +
                "                \"networkId\": \"26202\",\n" +
                "                \"networkName\": \"Vodafone.de\",\n" +
                "                \"networkType\": \"2\",\n" +
                "                \"sampleSizeRSSI\": \"35481\"\n" +
                "            },\n" +
                "            \"type3G\": {\n" +
                "                \"averageRssiAsu\": \"13.209795\",\n" +
                "                \"averageRssiDb\": \"-102.790205\",\n" +
                "                \"networkId\": \"26202\",\n" +
                "                \"networkName\": \"Vodafone.de\",\n" +
                "                \"networkType\": \"3\",\n" +
                "                \"sampleSizeRSSI\": \"2049\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"network26207\": {\n" +
                "            \"type3G\": {\n" +
                "                \"averageRssiAsu\": \"7.571271\",\n" +
                "                \"averageRssiDb\": \"-108.428729\",\n" +
                "                \"networkId\": \"26207\",\n" +
                "                \"networkName\": \"o2 - de\",\n" +
                "                \"networkType\": \"3\",\n" +
                "                \"sampleSizeRSSI\": \"1905\"\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"network_type\": null,\n" +
                "    \"perMinuteCurrent\": 1,\n" +
                "    \"perMinuteLimit\": 10,\n" +
                "    \"perMonthCurrent\": 1,\n" +
                "    \"perMonthLimit\": 2000\n" +
                "}\n";

        try {
            jsonWlan = new JSONObject(testJson);
        } catch (JSONException e) {
            Log.d("debug", "Fehler beim Erstellen des Teststrings");
        }
        Log.d("debug", "TestString" +jsonWlan);
        //jsonWlan = json = asyncRequest.execute(urlWlan).get();

        try {
            tmp = jsonWlan.getJSONObject("networkRank");
        } catch (JSONException e) {
            Log.d("debug", "Fehler beim Erstellen tmp-Objekt");
        }

        wlanLevel= tmp.length();

        Log.d("debug", "Laenge"+ wlanLevel);
        return wlanLevel;
    }
}


