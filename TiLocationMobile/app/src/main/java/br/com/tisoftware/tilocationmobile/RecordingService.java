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

        Log.i(TAG, "Chamou Serviço de gravação");

        File file = new File(path);

        //file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);

        Date date = new Date();
        CharSequence sdf = DateFormat.format("dd-MM-yy-hh-mm-ss",date.getTime()); // Pega data e hora


        if(rec != null) {

            onDestroy();
        }

        if(rec == null) {

            rec = new MediaRecorder();

            // Áudio de ligação
            //rec.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

            rec.setAudioSource(MediaRecorder.AudioSource.MIC);
            rec.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            rec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // Saída
            rec.setOutputFile(file.getAbsolutePath() + "/" + sdf + ".3gp"); // Salva nome do arquivo com data e hora

            if(!status) {

                try {
                    rec.prepare();
                    status = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "Erro no start" + e.toString());
                }
                rec.start();
                Log.i(TAG, "Gravação efetuada");

            } else {
                Log.i(TAG, "Erro na gravação");
            }


        } else {
            Log.i(TAG, "MediaRecorder não foi limpo");
            onDestroy();
        }
        return START_NOT_STICKY;

    }

    public void onDestroy()
    {
        super.onDestroy();

        // Serviço é restaurado para iniciar uma nova gravação
        rec.stop();
        rec.reset();
        rec.release();
        rec = null;
        status = false;

        Log.i(TAG, "MediaRecorder limpo");

    }

}
