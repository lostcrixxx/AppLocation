package br.com.tisoftware.tilocationmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import static br.com.tisoftware.tilocationmobile.MainActivity.TAG;

public class RecordReceiver extends BroadcastReceiver {

    static Boolean recordStarted = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        try {

//           boolean callWait=pref.getBoolean("recordStarted",false);
            Bundle extras = intent.getExtras();
            String state = extras.getString(TelephonyManager.EXTRA_STATE);


            if (extras != null) {
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                    Log.d(TAG, "Ligação");

                    Intent reivToServ = new Intent(context, RecordingService.class);

                    context.startService(reivToServ);


                    recordStarted = true;

                    pref.edit().putBoolean("recordStarted", true).apply();
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                    Log.d(TAG, "Ligação finalizada ");
                    recordStarted = pref.getBoolean("recordStarted", false);

                    //if (recordStarted && l == 0) {
                    if (recordStarted) {

                        context.stopService(new Intent(context, RecordingService.class));

                        pref.edit().putBoolean("recordStarted", false).apply();
                    }

                }

            }
            
            } catch(Exception e){
                e.printStackTrace();
            }

    }
}
