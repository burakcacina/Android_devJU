package net.burak.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

import static net.burak.androidproject.AppConstants.PREFS;

/* This is Created
        by
      BURAK CACINA
*/

public class UpdateAccActivity extends AppCompatActivity {
    private EditText up_text_Longitude, up_text_Latitude;
    private String up_latitude, up_longitude;
    private String userId, accessToken;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        Button but1 = (Button) findViewById(R.id.deleteAccountbutton);
        Button but2 = (Button) findViewById(R.id.uptadeAcc);

        up_text_Latitude = (EditText) findViewById(R.id.up_latitude);
        up_text_Longitude = (EditText) findViewById(R.id.up_longitude);

        this.sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.userId = this.sharedPreferences.getString(AppConstants.PREF_USER_ID, null);
        this.accessToken = this.sharedPreferences.getString(AppConstants.PREF_ACCESS_TOKEN, null);

        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DeleteAccTask().execute();
            }
        });

        but2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new UpdateAccInfoTask().execute();
            }
        });
    }

    public class UpdateAccInfoTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(AppConstants.MJILIK_ENDPOINT + "/accounts/" + userId);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Host", AppConstants.MJILIK_HOST_ENDPOINT);
                httpURLConnection.setRequestMethod("PATCH");
                httpURLConnection.connect();
                JSONObject jsonParam = new JSONObject();

                up_latitude = up_text_Latitude.getText().toString();
                up_longitude = up_text_Longitude.getText().toString();

                jsonParam.put("longitude", up_latitude);
                jsonParam.put("latitude", up_longitude);

                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                out.write(jsonParam.toString());
                out.close();

                int HttpResult = httpURLConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {
                    return "Success";
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
            super.onPostExecute(result);
            if (result != null) {
                Intent intent = new Intent(UpdateAccActivity.this, HomeActivity.class);
                Toast.makeText(getApplicationContext(), "Updated Succesfully", Toast.LENGTH_LONG).show();
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Failed Update", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class DeleteAccTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(AppConstants.MJILIK_ENDPOINT + "/accounts/" + userId);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                httpURLConnection.setRequestProperty("Host", AppConstants.MJILIK_HOST_ENDPOINT);
                httpURLConnection.setRequestMethod("DELETE");
                httpURLConnection.connect();

                int HttpResult = httpURLConnection.getResponseCode();
                System.out.println(HttpResult);
                if (HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {
                    return "DELETED";
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Intent intent = new Intent(UpdateAccActivity.this, MainActivity.class);
                Toast.makeText(getApplicationContext(), "Deleted Succesfully", Toast.LENGTH_LONG).show();
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Failed Delete", Toast.LENGTH_LONG).show();
            }
        }
    }

}
