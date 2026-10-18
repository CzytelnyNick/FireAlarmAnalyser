package com.example.ppozalarm;

import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button button1 = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button3);
        Button button3 = findViewById(R.id.button4);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGetRequest();

            }
        });
        // Wyślij zapytanie GET do ESP8266
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendSetAlarmRequest("false");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                sendGetRequest();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendSetResetRequest("true");
                    sendGetRequest();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void sendSetResetRequest(String resetStatus) throws IOException{
        String url = "http://192.168.100.188/setReset?reset=" + resetStatus;
        URL obj = new URL(url);
        Log.i("ResetStatus", url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        InputStream inputStream = con.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuilder response = new StringBuilder();
        if (resetStatus.equals("true")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendSetResetRequest("false");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);  // Opóźnienie 1 sekundy
        }

    }
    private void sendSetAlarmRequest(String alarmStatus) throws IOException {
        String url = "http://192.168.100.188/setAlarm?alarm=" + alarmStatus;
        URL obj = new URL(url);
        Log.i("AlarmStatus", url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        InputStream inputStream = con.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuilder response = new StringBuilder();
    }
    private void sendGetRequest() {
        TextView textView1 = findViewById(R.id.textView3);
        TextView textView2 = findViewById(R.id.textView4);

        try {

            // Adres IP ESP8266, który otrzymałeś w monitorze szeregowym

            String url = "http://192.168.100.188/";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            Log.i("TESTO", "1");
            // Ustawienia zapytania
            con.setRequestMethod("GET");
            Log.i("TESTO", "2");
            // Odczyt odpowiedzi z ESP8266
            InputStream inputStream = con.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {

                response.append(inputLine);
            }
            in.close();
            // Wyświetlenie odpowiedzi
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(this, 1000);
                    textView1.setText("Status: Połączono");
                    Log.d("HTTP_RESPONSE", response.toString());
                    if(response.toString().equals("true")){
                     textView2.setText("Status alarmu: Aktywny");
                    }
                    else {
                        Log.d("ALARM_STATUS", String.valueOf(response.toString().equals("true")));
                        textView2.setText("Status alarmu: Brak");
                    }
                }
            };
            handler.post(runnable);
        } catch (Exception e) {
            textView1.setText("Status: Brak połączenia ");
            Log.e("ERRORXD", e.toString()) ;
        }
    }
}