package net.burak.loginupdatesignup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class DeleteUserActivity extends AppCompatActivity {
    String access_token,response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(DeleteUserActivity.this);
        final String token = prefs2.getString("access_token", "no id");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DeleteUserActivity.this);
        final String iduser = prefs.getString("USERID", "no id");

        final String URL_TO_HIT = "http://52.211.99.140/api/v1/accounts/"+iduser;
        access_token =token;
        new JSONTask().execute(URL_TO_HIT);

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
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + access_token);
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                httpURLConnection.setRequestMethod("DELETE");
                httpURLConnection.connect();

                int HttpResult = httpURLConnection.getResponseCode();
                System.out.println(HttpResult);
                if (HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {
                    response = "DELETED";
                    return response;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }  finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            return null;
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(response != null)
            {
                Intent intent = new Intent(DeleteUserActivity.this, MainActivity.class);
                Toast.makeText(getApplicationContext(), "Deleted Succesfully", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Redirecting Recipe Page", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        }
    }
}
