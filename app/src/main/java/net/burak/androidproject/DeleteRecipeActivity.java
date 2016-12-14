package net.burak.androidproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/* This is Created
        by
      BURAK CACINA
*/

public class DeleteRecipeActivity extends AppCompatActivity {
    String access_token,response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs2.getString("access_token", "no id");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int recipeID = prefs.getInt("RECIPEID", 1);

        String URL_TO_HIT = "http://52.211.99.140/api/v1/recipes/" + recipeID;
        access_token = token;

        new JSONTask().execute(URL_TO_HIT);  //Executing Class
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            HttpURLConnection httpURLConnection = null;
            String result;

            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + access_token);
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                httpURLConnection.setRequestMethod("DELETE");
                httpURLConnection.connect();

                int HttpResult = httpURLConnection.getResponseCode();

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
            if(response != null) {
                Intent intent = new Intent(DeleteRecipeActivity.this, HomeActivity.class);
                Toast.makeText(getApplicationContext(), "Recipe Deleted", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        }
    }
}

