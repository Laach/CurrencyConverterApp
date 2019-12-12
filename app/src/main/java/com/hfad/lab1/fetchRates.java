package com.hfad.lab1;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class fetchRates extends AsyncTask<Void,Void,Void> {
    String jsonString="";

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("http://data.fixer.io/api/latest?access_key=0a47a88786b6d0f7e230ea00405d84d5");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine = "";

            jsonString = "dhasjdasijdkasjdkasjdaskl";



            while ((inputLine = bufferedReader.readLine()) != null) {
                jsonString += inputLine;
            }

/*
            JSONObject jsonResult = new JSONObject(fullStr);
            JSONObject jsonRates = jsonResult.getJSONObject("rates");

            ratesMap.put("USD", jsonRates.getDouble("USD"));
            testOutput.setText(ratesMap.get("USD").toString());

                Iterator<String> keysIterator = jsonRates.keys();
                while(keysIterator.hasNext()){
                    String key = keysIterator.next();
                    Double value = Double.valueOf(jsonRates.getString(key));

                    ratesMap.put(key,value);
                }*/

            connection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        MainActivity.testOutput.setText(jsonString);
    }
}
