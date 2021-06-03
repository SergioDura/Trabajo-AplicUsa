package es.upv.etsit.aatt.aemet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Url Peleas de Abajo, provincia de Zamora
        String url = "https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/horaria/49148/?api_key=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzZXJnaW9kdWNhc0BnbWFpbC5jb20iLCJqdGkiOiJmYzA4YjYxYy0zNTMzLTRhZmUtYjFkZS01OTBjOTkyNWU1MzIiLCJpc3MiOiJBRU1FVCIsImlhdCI6MTYyMTUwMDQ2MiwidXNlcklkIjoiZmMwOGI2MWMtMzUzMy00YWZlLWIxZGUtNTkwYzk5MjVlNTMyIiwicm9sZSI6IiJ9.GOb2Smj3AqPBDB1Dideo07HeKnifkZ8Xhzz62t8nk48";
        ServiciosWebEncadenados servicioWeb = new ServiciosWebEncadenados(url);
        servicioWeb.start();
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(firstFragment);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.firstFragment:
                    loadFragment(firstFragment);
                    return true;
                case R.id.secondFragment:
                    loadFragment(secondFragment);
                    return true;
                case R.id.thirdFragment:
                    loadFragment(thirdFragment);
                    return true;
            }
            return false;

        }
    };
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    // LLeva a cabo dos peticiones de servicios web encadenadas
    class ServiciosWebEncadenados extends Thread {

        String url_inicial;

        // constructor
        ServiciosWebEncadenados(String url_inicial) {
            this.url_inicial = url_inicial;
        }

        // tarea a ejecutar en hilo paralelo e independiente

        @Override public void run() {


            //------------------------------------------------------------------------------------------
            //------------------------------------------------------------------------------------------
            //------------------------------------------------------------------------------------------

            // Primera peticion
            // Hacemos una petición, incluyendo en la url, la api_key y el código del pueblo del cual queremos obtener la información.
            String respuesta = API_REST(url_inicial);
            //------------------------------------------------------------------------------------------


            try {
                String localidad = "Peleas de Abajo, Zamora";
                TextView localidad1 = (TextView)findViewById(R.id.pueblo);
                localidad1.setText(localidad);

                JSONObject objeto0 = new JSONObject(respuesta);
                String datos = objeto0.getString("datos");
                // Sacamos la url de los datos que nos interesan
                String respuesta2 = API_REST(datos);
                JSONArray obj1 = new JSONArray(respuesta2);
                //--------------------------------------------------------------------------------

                // Temperatura
                // 0 hoy 1 mañana 2 pasado mañana
                JSONObject temp = obj1.getJSONObject(0).getJSONObject("prediccion").getJSONArray("dia").getJSONObject(0).getJSONArray("temperatura").getJSONObject(9);

                String temp1 = temp.getString("value");

                System.out.println("La temperatura es:\n" + temp1);

                String mfinal1 = String.format("La temperatura es de %s grados.",temp1);
                TextView Temperatura = (TextView)findViewById(R.id.Temperatura);
                Temperatura.setText(mfinal1);

                //--------------------------------------------------------------------------------
                //--------------------------------------------------------------------------------
                //--------------------------------------------------------------------------------

                // Viento
                // 0 hoy 1 mañana 2 pasado mañana
                JSONObject viento = obj1.getJSONObject(0).getJSONObject("prediccion").getJSONArray("dia").getJSONObject(0).getJSONArray("vientoAndRachaMax").getJSONObject(18);

                //--------------------------------------------------------------------------------

                String viento1 = viento.getString("velocidad");
                String viento2 =viento.getString("direccion");
                System.out.println("La velocidad viento es:\n" + viento1);
                System.out.println("La direccion del viento es:\n." + viento2);

                String mfinal2 = String.format("La velocidad del viento es %s km/h.",viento1);
                String mfinal3 = String.format("La direccion del viento es: %s.",viento2);

                TextView vviento = (TextView)findViewById(R.id.vviento);
                vviento.setText(mfinal2);
                TextView dviento = (TextView)findViewById(R.id.dviento);
                dviento.setText(mfinal3);

                //--------------------------------------------------------------------------------
                // Cielo
                // 0 hoy 1 mañana 2 pasado mañana
                JSONObject cielo = obj1.getJSONObject(0).getJSONObject("prediccion").getJSONArray("dia").getJSONObject(0).getJSONArray("estadoCielo").getJSONObject(10);
                String cielo1 = cielo.getString("descripcion");
                System.out.println("El cielo está :\n" + cielo1);
                String mfinal4 = String.format("El cielo está %s.",cielo1);
                TextView estadocielo = (TextView)findViewById(R.id.estadocielo);
                estadocielo.setText(mfinal4);

                //--------------------------------------------------------------------------------

                // Precipitacion
                // 0 hoy 1 mañana 2 pasado mañana
                JSONObject preci = obj1.getJSONObject(0).getJSONObject("prediccion").getJSONArray("dia").getJSONObject(0).getJSONArray("probPrecipitacion").getJSONObject(1);
                String preci1 = preci.getString("value");
                System.out.println("Hay un %s de probabilidad de precipitación:\n" + preci1);

                String mfinal5 = String.format("Hay un %s de probabilidad de precipitación.",preci1);
                TextView precip = (TextView)findViewById(R.id.preci);
                precip.setText(mfinal5);

            } catch (JSONException e) {
                e.printStackTrace();

            }

            /* runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    printParametros(datos);
                } // Imprimimos todos los datos que tenemos
            });

        } // run*/
        }

    } // ServiciosWebEncadenados

    // Imprime parámetros meteorológicos en pantalla: esto ya se ejecutará en la UI
    public void printParametros(String respuesta2) {
        System.out.println("La respuesta es:\n" + respuesta2);

    }


    /** La peticion del argumento es recogida y devuelta por el método API_REST
     *  Método ya completado y supuestamente correcto */

    public String API_REST(String uri){

        StringBuffer response = null;

        try {
            URL url = new URL(uri);
            //Log.d(TAG, "URL: " + uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Detalles de HTTP
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            //Log.d(TAG, "Codigo de respuesta: " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream() , "ISO-8859-15" ));
                String output;
                response = new StringBuffer();

                while ((output = in.readLine()) != null) {
                    response.append(output);
                }
                in.close();
            } else {
                //Log.d(TAG, "responseCode: " + responseCode);
                return null; // retorna null anticipadamente si hay algun problema

            }
        } catch(Exception e) { // Posibles excepciones: MalformedURLException, IOException y ProtocolException
            e.printStackTrace();
            //Log.d(TAG, "Error conexión HTTP:" + e.toString());
            return null;
        }

        return response.toString(); // de StringBuffer -response- pasamos a String

    } // API_REST
}