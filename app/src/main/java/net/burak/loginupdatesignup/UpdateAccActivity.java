package net.burak.loginupdatesignup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateAccActivity extends AppCompatActivity {
    EditText up_text_Longitude,up_text_Latitude;
    String access_token,UPD_URL,up_latitude,up_longitude,response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Button but2 = (Button) findViewById(R.id.uptadeAcc);
        up_text_Latitude = (EditText)findViewById(R.id.up_latitude);
        up_text_Longitude = (EditText)findViewById(R.id.up_longitude);

        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(UpdateAccActivity.this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(UpdateAccActivity.this);

        final String token = prefs2.getString("access_token", "no id");
        final String iduser = prefs.getString("USERID", "no id");
        final String URL_TO_HIT = "http://52.211.99.140/api/v1/accounts/"+iduser;
        UPD_URL=URL_TO_HIT;
        access_token =token;

        but2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new UpdateAccActivity.JSONTask().execute(URL_TO_HIT);
            }
        });
    }
    public class JSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(UPD_URL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + access_token);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                httpURLConnection.setRequestMethod("PATCH");
                httpURLConnection.connect();
                JSONObject jsonParam = new JSONObject();

                up_latitude =up_text_Latitude.getText().toString();
                up_longitude =up_text_Longitude.getText().toString();

                jsonParam.put("longitude", up_latitude);
                jsonParam.put("latitude", up_longitude);

                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                out.write(jsonParam.toString());
                out.close();

                int HttpResult = httpURLConnection.getResponseCode();

                if (HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {
                    Intent intent = new Intent(UpdateAccActivity.this, HomeActivity.class);
                    startActivity(intent);
                    return response;
                }
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
        protected void onPostExecute(String result) {
            super.onPostExecute(response);
            Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
        }
    }
}
