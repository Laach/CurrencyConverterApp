package com.hfad.lab1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    EditText inputField;
    TextView outputField;
    Spinner fromCurrency;
    Spinner toCurrency;
    public static List<String> spinnerArray = new ArrayList<>();
    public static  ArrayAdapter<String> spinnerArrayAdapter;
    public static TextView testOutput;
    public static HashMap<String, Double> ratesMap = new HashMap<>();
    Location lastLocation = null;
    LocationManager locationManager = null;
    public static String base = "EUR";
    private static final int REQUEST_LOCATION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = findViewById(R.id.input);
        outputField = findViewById(R.id.output);
        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);
        testOutput = findViewById(R.id.testOutput);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        }
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //testOutput.setText(String.valueOf(lastLocation));

        base = getCountryCurrency(lastLocation);

        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String url = "http://data.fixer.io/api/latest?access_key=0a47a88786b6d0f7e230ea00405d84d5";
        //backup api if fixer doesn't work
        String url2 = "https://api.exchangeratesapi.io/latest";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                testOutput.setText("fail");
                call.cancel();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(myResponse);
                                JSONObject ratesJson = jsonObject.getJSONObject("rates");

                                Iterator<String> iterator = ratesJson.keys();
                                while(iterator.hasNext()){
                                    String key = iterator.next();
                                    Double value = ratesJson.getDouble(key);

                                    ratesMap.put(key,value);
                                    spinnerArray.add(key);
                                    spinnerArrayAdapter.notifyDataSetChanged();
                                }

                                //Set selected from base currency
                                fromCurrency.setSelection(spinnerArray.indexOf(base));
                                toCurrency.setSelection(spinnerArray.indexOf("EUR"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });


        toCurrency.setAdapter(spinnerArrayAdapter);
        fromCurrency.setAdapter(spinnerArrayAdapter);

        AdapterView.OnItemSelectedListener adapter = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String inputString = inputField.getText().toString();
                convert(inputString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        fromCurrency.setOnItemSelectedListener(adapter);
        toCurrency.setOnItemSelectedListener(adapter);

        inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputString = s.toString();
                convert(inputString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_LOCATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }

        }
    }

    public String getCountryCurrency(Location loc){
        String countryCode;
        String countryCurrency = "EUR";
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if(addresses.size() > 0){
                countryCode = addresses.get(0).getCountryCode();
                countryCurrency = Currency.getInstance(new Locale("", countryCode)).getCurrencyCode();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return countryCurrency;
    }

    //start new intent that shows all conversion rates
    public void showConversionRates(View view){
        Intent intent = new Intent(this, ShowConversionRatesActivity.class);
        startActivity(intent);
    }

    public void convert(String inputString){
        //check if empty string to avoid crashes
        if(inputString.length() > 0){
            int value = Integer.parseInt(inputString);

            String from = fromCurrency.getSelectedItem().toString();
            String to = toCurrency.getSelectedItem().toString();

            double rateToEUR;
            double rateFromEUR;
            double newValue = 0;
            double valueInEUR = 0;

            if(from == to){
                outputField.setText(String.valueOf(value));
            }else{
                //convert to EUR
                rateToEUR = ratesMap.get(from);
                valueInEUR = value / rateToEUR;
                //convert to new currency
                rateFromEUR = ratesMap.get(to);
                newValue = valueInEUR * rateFromEUR;
                outputField.setText(String.valueOf(newValue));
            }
        }
        else{
            //reset text if input is empty
            outputField.setText("0");
        }
    }
}
