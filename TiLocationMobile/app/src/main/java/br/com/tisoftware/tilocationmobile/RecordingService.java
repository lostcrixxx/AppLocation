package br.com.tisoftware.tilocationmobile;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
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

        //file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);

        Date date = new Date();
        CharSequence sdf = DateFormat.format("dd-MM-yy-hh-mm-ss",date.getTime()); // Pega data e hora
        rec = new MediaRecorder();
        rec.reset();

        // Áudio de ligação
        //rec.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

        rec.setAudioSource(MediaRecorder.AudioSource.MIC);
        rec.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        rec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // Saída
        rec.setOutputFile(file.getAbsolutePath()+ "/" + sdf+".3gp"); // Salva nome do arquivo com data e hora

            try {
                rec.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
            rec.start();
            Log.i(TAG, "Gravação efetuada");


        return START_NOT_STICKY;


    }

    public void onDestroy()
    {
        super.onDestroy();

        // Serviço é restaurado para iniciar uma nova gravação
        rec.stop();
        rec.reset();
        rec.release();
        rec=null;

        Log.d(TAG, "onDestroy: "+"Gravação finalizada");

    }

}
