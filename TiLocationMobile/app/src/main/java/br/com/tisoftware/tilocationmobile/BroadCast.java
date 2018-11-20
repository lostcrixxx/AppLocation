package br.com.tisoftware.tilocationmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                Intent pushIntent = new Intent(context, Servico.class);
                context.startService(pushIntent);
            }

    }
}
