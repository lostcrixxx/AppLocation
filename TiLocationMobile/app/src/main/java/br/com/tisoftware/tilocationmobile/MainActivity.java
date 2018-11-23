package br.com.tisoftware.tilocationmobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.tisoftware.tilocationmobile.db.DatabaseHandler;
import br.com.tisoftware.tilocationmobile.db.DatabaseManager;

public class MainActivity extends AppCompatActivity {

    // Coordenadas
    private TextView t;
    private LocationManager locationManager;
    private LocationListener listener;
    private String data_Cadastro, latitude, longitude;

    // TODO salvar IMEI em SharedPreferences
    // IMEI
    private String IMEINumber = "";
    final int reqcode = 1;

    DatabaseHandler db=new DatabaseHandler(this);
    final static String TAG = "localizacao";
    RecordAdapter rAdapter;
    RecyclerView recycler;
    List<CallDetails> callDetailsList;
    boolean checkResume=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // coordenadas
        //t = (TextView) findViewById(R.id.textView);

        // TODO Iniciar serviço GPS
        //Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        //startService(i);

        // TODO Parar Serviço de GPS
        //Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        //stopService(i);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //t.append("\n " + location.getLongitude() + " " + location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                latitude = String.valueOf(location.getLatitude());
                Log.i("localizacao", "Exibindo na tela");
                imei();
                registrar(); // Enviar os dados para o banco
                Log.i("localizacao", "Chamou para registrar no banco");
                olhaData();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        // Verifica se o GPS está ativado
        verificaGPS();

        // TODO Verificar, porque não está pegando o IMEI antes das permissões
        // Chama método para pegar IMEI
        //imei();


        /*StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());*/

      /*  if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }*/

        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putInt("numOfCalls",0).apply();

       // pref.edit().putInt("serialNumData", 1).apply();

        //rAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Check", "onResume: ");
        if(checkPermission()) {
            Log.i(TAG, "Está com todas as permissões");
            //Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();
            if(checkResume==false) {
                setUi();
                // this.callDetailsList=new DatabaseManager(this).getAllDetails();
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

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        MenuItem item=menu.findItem(R.id.mySwitch);

        View view = getLayoutInflater().inflate(R.layout.switch_layout,null,false) ;

        final SharedPreferences pref1= PreferenceManager.getDefaultSharedPreferences(this);

        SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.switchCheck);
        switchCompat.setChecked(pref1.getBoolean("switchOn",true));
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.d("Switch", "onCheckedChanged: " +isChecked);
                    Toast.makeText(getApplicationContext(), "Call Recorder ON", Toast.LENGTH_LONG).show();
                    pref1.edit().putBoolean("switchOn",isChecked).apply();
                }else{
                    Log.d("Switch", "onCheckedChanged: " +isChecked);
                    Toast.makeText(getApplicationContext(), "Call Recorder OFF", Toast.LENGTH_LONG).show();
                    pref1.edit().putBoolean("switchOn",isChecked).apply();
                }
            }
        });
        item.setActionView(view);
        return true;
    }

    public void setUi()
    {
        recycler=(RecyclerView) findViewById(R.id.recyclerView);
        callDetailsList=new DatabaseManager(this).getAllDetails();

        for(CallDetails cd:callDetailsList)
        {
            String log="Phone num : "+cd.getNum()+" | Time : "+cd.getTime1()+" | Date : "+cd.getDate1();
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
                i++;
            else {
                reqPerm.add(permis);
            }
        }

        // TODO Verificar a quantidade de permissoes
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
                Toast.makeText(getApplicationContext(), "Phone Permissions needed for " + permissions, Toast.LENGTH_LONG);
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
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {


                    Log.i(TAG, "Tem permissão de acesso nas chamadas");
                    //Toast.makeText(getApplicationContext(),"Permission Granted to access Phone calls",Toast.LENGTH_LONG);
                }
                else
                Log.i(TAG, "Não pode acessar as chamadas");
                    //Toast.makeText(getApplicationContext(),"You can't access Phone calls",Toast.LENGTH_LONG);
                break;
            case 10:
                imei();
                verificaGPS();
                break;
            default:
                break;
        }

    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                verificaGPS();
                break;
            default:
                break;

        }

    }
    */


    void verificaGPS() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }

        // 60000 = 1 minuto, 300000 = 5 minutos, 600000 = 10 minutos
        locationManager.requestLocationUpdates("gps", 60000, 0, listener);
    }

    // Pegar IMEI do aparelho
    void imei() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            String[] per = {Manifest.permission.READ_PHONE_STATE};
            requestPermissions(per, reqcode);

            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    IMEINumber = tm.getImei();
                    //textView.setText(IMEINumber);
                    Log.i("localizacao","IMEI: " + IMEINumber);
                }
            } else {
                IMEINumber = tm.getDeviceId();
                //.setText(IMEINumber);
                Log.i("localizacao","IMEI else: " + IMEINumber);
            }

        }
    }

    String olhaData() {
        // Data e Hora
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        // Hora
        // SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");
        // String hora_atual = dateFormat_hora.format(data_atual);

        Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        data_Cadastro = dateFormat.format(data_atual);

        Log.i("localizacao", "data_atual: " + data_Cadastro);
        //Log.i("localizacao", "data_atual" + data_atual.toString());

        return data_Cadastro;
    }


    // Enviar informações para o banco de dados
    private void registrar() {
        final ProgressDialog loading = ProgressDialog.show(this, "Por favor espere...", "Atualizando dados...", false, false);
        String REGISTER_URL = "http://tilocationmobile.atspace.cc/insert.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Log.i("localizacao","Cadastrado com sucesso");
                        //Toast.makeText(getApplicationContext(), "Cadastrado com sucesso", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Log.i("localizacao","Erro para inserir no banco");
                        //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("imei", IMEINumber);
                params.put("longitude", longitude);
                params.put("latitude", latitude);
                params.put("dataCadastro", data_Cadastro);
                Log.i("localizacao","Todos os dados");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // TODO Fechar aplicativo e iniciar servicos background
    // Menu de configurações ou tela principal
    //startService(new Intent(LocationActivity.this,ServicoTest.class));


}
