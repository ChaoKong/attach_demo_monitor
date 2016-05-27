package com.example.chaokong.attackdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button monitorBtn;

    private SharedPreferences sp_monitor,sp1_monitor;
    private SharedPreferences.Editor spEditor_monitor;
    private String monitor_state = "FALSE";
    Context context;

    String TAG = "Monitor_app";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        monitorBtn = (Button) findViewById(R.id.monitor_switch);
        context= this;

    }


    @Override
    public void onResume() {
        super.onResume();
        monitor_state = getAudioState(context);
        if (monitor_state.equals("FALSE"))
        {
            monitorBtn.setText("monitoring stopped(push to start)");
        }
        else
        {
            monitorBtn.setText("Monitoring(push to stop)");
        }


    }

    public void MonitorSwitch(View view) throws IOException {

        monitor_state = getAudioState(context);
        Log.d(TAG,"previous monitor state:   "+ monitor_state);
        if (monitor_state.equals("FALSE"))
        {
            monitorBtn.setText("Monitoring(push to stop)");
            updateAudioState("TRUE",context);
            Intent it = new Intent();
            it.setAction("active_monitor");
            String tmp_intent_mes = "START";
            it.putExtra("monitor", tmp_intent_mes);
            it.setClass(this, MyService.class);
            this.startService(it);
        }
        else
        {
            monitorBtn.setText("monitoring stopped(push to start)");
            updateAudioState("FALSE",context);
            Intent it = new Intent();
            it.setAction("active_monitor");
            String tmp_intent_mes = "STOP";
            it.putExtra("monitor", tmp_intent_mes);
            it.setClass(this, MyService.class);
            this.stopService(it);

        }

    }



    public void updateAudioState(String state,Context context){
        sp_monitor = PreferenceManager.getDefaultSharedPreferences(context);
        spEditor_monitor = sp_monitor.edit();
        spEditor_monitor.putString("monitor_state", state);
        spEditor_monitor.commit();
        Log.d(TAG,"current monitor state: "+state);

    }

    public String getAudioState(Context context){
        sp1_monitor = PreferenceManager.getDefaultSharedPreferences(context);
        String st =sp1_monitor.getString("monitor_state", "FALSE");
        return st;
    }

}
