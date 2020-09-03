package com.example.whatisweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);
    }

    public  void getWeather (View view){
        try {
            DownloadTask task = new DownloadTask();
            String enCodedCityName = URLEncoder.encode(editText.getText().toString() , "UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + enCodedCityName +"&appid=439d4b804bc8187953eb36d2a8c26a02");

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken() , 0);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find :( " , Toast.LENGTH_SHORT ).show();

        }
    }

    public class DownloadTask extends AsyncTask<String , Void , String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char)data;
                    stringBuilder.append(current);
                    data = reader.read();
                }
                result = stringBuilder.toString();
                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("weather content",weatherInfo);
                String message = "";

                JSONArray arr = new JSONArray(weatherInfo);
                for(int i=0; i<arr.length(); i++){

                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if(!main.equals("")  && !description.equals("")){
                        message += main + " : " + description + "\r\n";
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Could not find :( " , Toast.LENGTH_SHORT ).show();
                    }
                }
                if(!message.equals("")){
                    resultTextView.setText(message);
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find :( " , Toast.LENGTH_SHORT ).show();
            }
        }
    }


}