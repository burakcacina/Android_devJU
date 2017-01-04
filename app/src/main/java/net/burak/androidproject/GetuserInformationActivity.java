package net.burak.androidproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetuserInformationActivity extends AppCompatActivity {

    private TextView tvlatitude;
    private TextView tvlongitude;
    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinformation);

        tvUsername = (TextView) findViewById(R.id.tvuserName);
        tvlatitude = (TextView) findViewById(R.id.tvlatitude);
        tvlongitude = (TextView) findViewById(R.id.tvlongitude);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GetuserInformationActivity.this);
        final String iduser = prefs.getString("USERID", "no id");

        final String URL_TO_HIT = "http://52.211.99.140/api/v1/accounts/" + iduser;

        new JSONTask().execute(URL_TO_HIT);


    }
    public class JSONTask extends AsyncTask<String, String, JSONTask.Response> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Response doInBackground(String... params) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;
            Response r = new Response();

            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();


                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String line = null;

                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();
                    JSONObject myJson = new JSONObject(sb.toString());
                    r.response_id = myJson.optString("id");
                    r.response_username = myJson.optString("userName");
                    r.response_latitude = myJson.optDouble("latitude");
                    r.response_longitude = myJson.optDouble("longitude");
                    System.out.println(r.response_latitude);
                    System.out.println(sb.toString());
                    return r;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            return null;
        }

        public class Response {
            public String response_username;
            public String response_id;
            public double response_longitude;
            public double response_latitude;

        }

        protected void onPostExecute(final Response r) {

            if(r != null) {
                if (r.response_latitude == 0.0 && r.response_longitude == 0.0) {
                    Toast.makeText(getApplicationContext(), "Redirected because you dont have Longitude and Latitude", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Update your account", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(GetuserInformationActivity.this, UpdateAccActivity.class);
                    startActivity(intent);
                } else {

                    tvUsername.setText("User: " + r.response_username);
                    tvlatitude.setText("Latitude: " + String.valueOf(r.response_latitude));
                    tvlongitude.setText("Longitude: " + String.valueOf(r.response_longitude));

                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "ERROR: Server did not return a response", Toast.LENGTH_LONG).show();
            }
        }
    }
}
