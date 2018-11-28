package com.example.bpr.app2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void printChart(String [] data, TextView textViewToChange){

        Float huminidity = Float.parseFloat(data[2]);

        PieChartView pieChartView = findViewById(R.id.huminidity_chart);
        List<SliceValue> pieData = new ArrayList<>();




        pieData.add(new SliceValue(huminidity, Color.GREEN).setLabel(huminidity + " %"));
        pieData.add(new SliceValue(100-huminidity, Color.GRAY).setLabel(""));
        PieChartData pieChartData = new PieChartData(pieData);

        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartView.setPieChartData(pieChartData);
        //pieChartData.setHasLabels(true);



        textViewToChange.setText(data[0]);

    }

    private void updateInformation(){

        if(!ready) return;

       final TextView textViewToChange = (TextView) findViewById(R.id.textView);

        ready = false;
        textViewToChange.setText("Run command");

        try{
            AM2302 var = new AM2302();
            String [] result = var.infoUpdate();
            //textViewToChange.setText(result);
            printChart(result, textViewToChange);
        }
        catch (Exception e) {
            textViewToChange.setText("Exception occured in:  buttonUpdateClick function");
            ready = true;
        }
        //textViewToChange.setText("Waiting...");
        ready = true;

    }


    private static Boolean ready = true;
    public void buttonUpdateClick(View v){

        updateInformation();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
