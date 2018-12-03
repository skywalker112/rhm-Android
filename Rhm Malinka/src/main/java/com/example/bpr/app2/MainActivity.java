package com.example.bpr.app2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity implements Listener {

    private Thermometer thermometer;
    private float temperature;


    private View loadingPanel, buttonUpdate;
    private TextView debug;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thermometer = (Thermometer) findViewById(R.id.thermometer);

        loadingPanel = findViewById(R.id.loadingPanel);
        buttonUpdate = findViewById(R.id.updateButton);
        debug = (TextView) findViewById(R.id.debug);

        // initialize pieChartView
        PieChartView pieChartView = findViewById(R.id.humChart);
        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue(100, getResources().getColor(R.color.huminidity_rest)).setLabel(""));
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartView.setPieChartData(pieChartData);

        buttonUpdateClick(findViewById(R.id.updateButton));
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void printChart(String [] data, TextView debug){

        if(data.length != 3){
            debug.setText(data[0]);
        }
        else{
            Float huminidity = Float.parseFloat(data[2]);
            Float temperature = Float.parseFloat(data[1]);
            String date = data[0];

            // huminidity
            PieChartView pieChartView = findViewById(R.id.humChart);
            List<SliceValue> pieData = new ArrayList<>();

            pieData.add(new SliceValue(huminidity, getResources().getColor(R.color.green)).setLabel(huminidity + " %"));
            pieData.add(new SliceValue(100-huminidity, getResources().getColor(R.color.huminidity_rest)).setLabel(""));
            PieChartData pieChartData = new PieChartData(pieData);

            pieChartData.setHasLabels(true).setValueLabelTextSize(14);
            pieChartView.setPieChartData(pieChartData);
            pieChartData.setHasLabels(true);

            // date
            TextView showDate = (TextView) findViewById(R.id.dateTextView);
            showDate.setText(date);

            // temperature
            TextView showTemp = (TextView) findViewById(R.id.tempShow);
            showTemp.setText(temperature + " ℃");
            thermometer.setCurrentTemp(temperature);
        }
    }


    private void updateInformation(){
        TextView debug = (TextView) findViewById(R.id.debug);
        debug.setText("");

        try{
            AM2302 var = new AM2302(this);
            var.infoUpdate();
        }
        catch (Exception e) {
            debug.setText("Exception occured in:  buttonUpdateClick function");
        }
    }


    private static Boolean ready = true;
    public void buttonUpdateClick(View buttonUpdate){

        if(ready == false) return;

        setVisible(loadingPanel, true);
        setVisible(buttonUpdate, false);

        updateInformation();
    }

    @Override
    public void onResponseReceived(final String[] result)
    {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                printChart(result, debug);
                setVisible(buttonUpdate, true);
                setVisible(loadingPanel, false);

            }
        });
    }

    private void setVisible(View element, Boolean state){
        if(state == true){
            element.setVisibility(View.VISIBLE);
        }
        else{
            element.setVisibility(View.INVISIBLE);
        }
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
