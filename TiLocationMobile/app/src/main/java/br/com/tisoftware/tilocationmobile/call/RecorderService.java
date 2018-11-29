package br.com.tisoftware.tilocationmobile.call;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;


public class RecorderService extends Service {

    // Classe para gravar áudios ou vídeos
    MediaRecorder recorder;
    static final String TAGS=" Inside Service";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {

        recorder = new MediaRecorder();
        recorder.reset();

        String phoneNumber=intent.getStringExtra("number");
        Log.d(TAGS, "Phone number in service: "+phoneNumber);

        // Pegar hora da ligação
        String time=new CommonMethods().getTIme();

        // CAminho para o áudio
        String path=new CommonMethods().getPath();

        // Caminho + telefone + hora + .mp4(formato de saída)
        String rec=path+"/"+phoneNumber+"_"+time+".mp4";

        //recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL); // API 19 Android 4.4
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        // Saída no formato 3GP
        //recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        recorder.setOutputFile(rec);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();

        Log.d(TAGS, "onStartCommand: "+"Recording started");

        return START_NOT_STICKY;
    }

    public void onDestroy()
    {
        super.onDestroy();

        // Serviço é restaurado para iniciar uma nova gravação
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder=null;

        Log.d(TAGS, "onDestroy: "+"Recording stopped");

    }
}
