package br.com.tisoftware.tilocationmobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GPS_Service extends Service {

    // Coordenadas
    private String data_Cadastro, latitude, longitude;

    // TODO salvar IMEI em SharedPreferences
    // IMEI
    private String IMEINumber = "";
    final int reqcode = 1;

    private LocationListener listener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location_update");
                i.putExtra("coordenadas", location.getLongitude()+ " "+location.getLatitude());
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                sendBroadcast(i);
                Log.i("localizacao", "Long: " + location.getLongitude()+ "  Lat: "+location.getLatitude());
                imei();
                olhaData();
                registrar();
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
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        // 300000 = 5 minutos
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0, listener);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null) {
            locationManager.removeUpdates(listener);
        }
    }

    // Pegar IMEI do aparelho
    void imei() {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        //{
        //    String[] per = {Manifest.permission.READ_PHONE_STATE};
        //    requestPermissions(per, reqcode);

            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    IMEINumber = tm.getImei();
                    //textView.setText(IMEINumber);
                    Log.i("localizacao","IMEI: < 23" + IMEINumber);
                }
            } else {
                IMEINumber = tm.getDeviceId();
                //.setText(IMEINumber);
                Log.i("localizacao","IMEI >= 23: " + IMEINumber);
            }

        //}
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
        //final ProgressDialog loading = ProgressDialog.show(this, "Por favor espere...", "Atualizando dados...", false, false);
        String REGISTER_URL = "http://tilocationmobile.atspace.cc/insert.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //loading.dismiss();
                        Log.i("localizacao","Cadastrado com sucesso");
                        //Toast.makeText(getApplicationContext(), "Cadastrado com sucesso", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loading.dismiss();
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
                Log.i("localizacao","Pegou todos os dados");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

