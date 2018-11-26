package br.com.tisoftware.tilocationmobile;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TestServico extends Service{

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("localizacao", "onStartCommand");
        // START_STICKY serve para executar seu serviço até que você pare ele, é reiniciado automaticamente sempre que termina

        Log.e("oi", "onStartCommand");
        boolean ok = true;
        while(ok == true){
            try {
                Thread.sleep(120000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gerarNotificacao();
        }

        return START_STICKY;
    }

    void gerarNotificacao() {
        Log.i("localizacao", "servico 1");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
