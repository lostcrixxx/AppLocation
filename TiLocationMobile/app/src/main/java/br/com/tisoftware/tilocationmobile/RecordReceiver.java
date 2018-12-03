package br.com.tisoftware.tilocationmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.telephony.TelephonyManager;
import android.util.Log;

import static br.com.tisoftware.tilocationmobile.MainActivity.TAG;

public class RecordReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        final String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) { // Incoming call

            Log.i(TAG,"Ligação: Recebendo");
            Intent pushIntent = new Intent(context, RecordingService.class);
            context.startService(pushIntent);

        } else  if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) { // Outgoing call

            Log.i(TAG,"Ligação: Ligando");
            Intent pushIntent = new Intent(context, RecordingService.class);
            context.startService(pushIntent);

        } else  if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) { // Call ended
            Log.i(TAG,"Ligação: Desligou");


        }



    }
}
