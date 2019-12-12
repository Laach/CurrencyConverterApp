package com.hfad.lab1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;

public class ShowConversionRatesActivity extends AppCompatActivity {
    Spinner selectedCurr;
    RecyclerView ratesBox;
    RecyclerAdapter recAdapter;
    public static String curr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversion_rates);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        selectedCurr = findViewById(R.id.selectedCurr);
        ratesBox = findViewById(R.id.ratesBox);

        //Use static adapter from MainActivity
        selectedCurr.setAdapter(MainActivity.spinnerArrayAdapter);
        selectedCurr.setSelection(MainActivity.spinnerArrayAdapter.getPosition(MainActivity.base));

        ratesBox.setLayoutManager(new LinearLayoutManager(this));
        recAdapter = new RecyclerAdapter(this, MainActivity.spinnerArray);
        ratesBox.setAdapter(recAdapter);

        curr = MainActivity.base;



        selectedCurr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curr = selectedCurr.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
