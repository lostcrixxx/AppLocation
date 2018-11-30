package br.com.tisoftware.tilocationmobile;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import java.util.Date;

import static br.com.tisoftware.tilocationmobile.MainActivity.TAG;


public class RecordingService extends Service {

    // Classe para gravar áudio ou vídeo
    private MediaRecorder rec;
    private boolean recordstarted;
    private boolean status = false;
    // Converte caminho para abstrato
    //private File file;
    // Caminho de uma pasta
    String path = "sdcard/alarms/";




    @Nullable
    @Override
    // Para interagir com serviço
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        File file = new File(path);
        Log.i(TAG, "Chamou Serviço de gravação");
        //file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);

        Date date = new Date();
        CharSequence sdf = DateFormat.format("dd-MM-yy-hh-mm-ss",date.getTime()); // Pega data e hora
        rec = new MediaRecorder();

        // Áudio de ligação
        //rec.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

        rec.setAudioSource(MediaRecorder.AudioSource.MIC);
        rec.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        rec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // Saída
        rec.setOutputFile(file.getAbsolutePath()+ "/" + sdf+".3gp"); // Salva nome do arquivo com data e hora

        // Serviço de telefone
        TelephonyManager manager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        manager.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                //super.onCallStateChanged(state, incomingNumber){
                // Alteração na ligação e não ter áudio
                //if(status) {
                    if (TelephonyManager.CALL_STATE_IDLE == state && rec != null && status) { // Estado da chamada do dispositivo: sem atividade.
                        Log.i(TAG, "sem atividade de ligações");
                        rec.stop();
                        rec.reset();
                        rec.release();
                        recordstarted = false;
                        status = false;
                        //stopSelf();

                    } else if (TelephonyManager.CALL_STATE_OFFHOOK == state) { // Estado da chamada do dispositivo: Fora do gancho.

                        try {
                            rec.prepare();

                        } catch (IOException e) {
                            rec.release();
                            //e.printStackTrace();
                        }
                        rec.start();
                        Log.i(TAG, "Gravação efetuada");
                        recordstarted = true;
                        status = true;

                    }
                    /*
                    else if(TelephonyManager.CALL_STATE_RINGING == state){ // Ligação recebida
                        try {
                            rec.prepare();

                        } catch (IOException e) {
                            rec.release();
                            //e.printStackTrace();
                        }
                        rec.start();
                        Log.i(TAG, "Gravação recebida");
                        recordstarted = true;
                        status = true;
                    }
                    */

                //}
            }


        }, PhoneStateListener.LISTEN_CALL_STATE);


        return START_STICKY;

    }



}
