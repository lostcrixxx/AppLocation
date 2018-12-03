package br.com.tisoftware.tilocationmobile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.tisoftware.tilocationmobile.call.CallDetails;
import br.com.tisoftware.tilocationmobile.call.RecordAdapter;
import br.com.tisoftware.tilocationmobile.db.DatabaseHandler;
import br.com.tisoftware.tilocationmobile.db.DatabaseManager;

public class MainActivity extends AppCompatActivity {

    DatabaseHandler db=new DatabaseHandler(this);
    RecordAdapter rAdapter;
    RecyclerView recycler;
    List<CallDetails> callDetailsList;
    boolean checkResume=false;

    final int reqcode = 1;

    // Log do aplicativo
    final static String TAG = "localizacao";

    //
    static boolean statusGPS = false;

    // Para carregar histórico de ligações na tela
    //boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        if(checkPermission()) {

            Log.i(TAG, "Passou na validação");

            Log.i(TAG, "Chamou GPS Service");
            Intent g = new Intent(getApplicationContext(), GPS_Service.class);
            startService(g);


            //Log.i(TAG, "Chamou Recording Service");
            //Intent r = new Intent(getApplicationContext(), RecordingService.class);
            //startService(r);


        } else {
            checkPermission();
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putInt("numOfCalls", 0).apply();

        // pref.edit().putInt("serialNumData", 1).apply();
        //rAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Check", "onResume: ");
        if(checkPermission()) {
            // Log.i(TAG, "Verificado novamente. Está com todas as permissões");
            //Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();
            if(checkResume==false) {
                // Atualizar apenas uma vez
                checkResume = true;

                // Exibir as ligações na tela do aplicativo
                setUi();
                this.callDetailsList=new DatabaseManager(this).getAllDetails();
                rAdapter.notifyDataSetChanged();

            }
        }
    }

    protected void onPause()
    {
        super.onPause();
        SharedPreferences pref3=PreferenceManager.getDefaultSharedPreferences(this);
        if(pref3.getBoolean("pauseStateVLC",false)) {
            checkResume = true;
            pref3.edit().putBoolean("pauseStateVLC",false).apply();
        }
        else
            checkResume=false;
    }


    public void setUi()
    {
        recycler=(RecyclerView) findViewById(R.id.recyclerView);
        callDetailsList=new DatabaseManager(this).getAllDetails();

        for(CallDetails cd:callDetailsList)
        {
            // Informações para o banco de dados
            String log="Número do telefone : "+cd.getNum()+" | Hora : "+cd.getTime1()+" | Data : "+cd.getDate1();
            Log.d("Database ", log);
        }

        Collections.reverse(callDetailsList);
        rAdapter=new RecordAdapter(callDetailsList,this);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(rAdapter);

    }


    private boolean checkPermission()
    {
        int i=0;
        String[] perm={Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
        List<String> reqPerm=new ArrayList<>();

        for(String permis:perm) {
            int resultPhone = ContextCompat.checkSelfPermission(MainActivity.this,permis);
            if(resultPhone== PackageManager.PERMISSION_GRANTED)
                i++; // Soma as permissoes aceitas
            else {
                reqPerm.add(permis);
            }
        }

        // Verificar a quantidade de permissoes
        if(i==8) {

            return true;
        }
        else
            return requestPermission(reqPerm);

    }


    private boolean requestPermission(List<String> perm)
    {
        // String[] permissions={Manifest.permission.READ_PHONE_STATE,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        String[] listReq=new String[perm.size()];
        listReq=perm.toArray(listReq);
        for(String permissions:listReq) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissions)) {
                Toast.makeText(getApplicationContext(), "Telefone precisa de permissão: " + permissions, Toast.LENGTH_LONG);
            }
        }

        ActivityCompat.requestPermissions(MainActivity.this, listReq, 1);

        return false;
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch(requestCode)
        {
            case 1:
                if(grantResults.length>0 && grantResults[5]==PackageManager.PERMISSION_GRANTED) {
                statusGPS = true;
                    // TODO validar todas as permissoes
                    Log.i(TAG, "Tem permissão de acesso nas chamadas");
                    if(statusGPS == true){

                        Log.i(TAG, "Chamou GPS Service");
                        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                        startService(i);
                    }

                    //Log.i(TAG, "Chamou Recording Service");
                    //Intent i = new Intent(getApplicationContext(), RecordingService.class);
                    //startService(i);
                }
                else
                    // TODO LOOP para solicitar as permissoes
                    //checkPermission();

                break;
            case 10:


                    //verificaGPS();

                break;
            default:
                break;
        }

    }


    // MENU configurações
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


        }

        return super.onOptionsItemSelected(item);
    }
}
