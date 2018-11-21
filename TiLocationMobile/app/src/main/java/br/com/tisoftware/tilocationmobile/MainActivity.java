package br.com.tisoftware.tilocationmobile;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Coordenadas
    private TextView t;
    private LocationManager locationManager;
    private LocationListener listener;
    private String data_Cadastro, latitude, longitude;

    // IMEI
    private String IMEINumber;
    final int reqcode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // coordenadas
        t = (TextView) findViewById(R.id.textView);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                t.append("\n " + location.getLongitude() + " " + location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                latitude = String.valueOf(location.getLatitude());
                Log.i("localizacao", "Exibindo na tela");
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

        // Chama método para pegar IMEI
        imei();

    }


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
        locationManager.requestLocationUpdates("gps", 300 * 1000, 0, listener);
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

                        Toast.makeText(getApplicationContext(), "cadastrado com sucesso", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("imei", IMEINumber);
                params.put("longitude", longitude);
                params.put("latitude", latitude);
                params.put("dataCadastro", data_Cadastro);
                Log.i("localizacao","Dados gravados");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // TODO Fechar aplicativo e iniciar servicos background
    // Menu de configurações ou tela principal
    //startService(new Intent(MainActivity.this,Servico.class));

}


