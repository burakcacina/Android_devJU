package net.burak.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import net.burak.androidproject.models.TokenModel;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static net.burak.androidproject.AppConstants.*;

/* This is Created
        by
      BURAK CACINA
*/

public class LoginActivity extends AppCompatActivity {

    private EditText ET_REG_USER_NAME, ET_REG_USER_PASS, ET_REG_USER_PASS2;
    private String reg_user_name, reg_user_pass;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ET_REG_USER_NAME = (EditText) findViewById(R.id.reg_user_name);
        ET_REG_USER_PASS = (EditText) findViewById(R.id.reg_user_pass);
        ET_REG_USER_PASS2 = (EditText) findViewById(R.id.reg_user_pass_confirmation);

        Button but1 = (Button) findViewById(R.id.userLog);

        this.sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new LoginTask().execute();
            }
        });

    }


    public class LoginTask extends AsyncTask<String, String, LoginTask.Response> {

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
            r.error = "Failed";

            try {
                URL url = new URL(MJILIK_ENDPOINT + "/tokens/password");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("Host", MJILIK_HOST_ENDPOINT);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                reg_user_pass = ET_REG_USER_PASS.getText().toString();
                reg_user_name = ET_REG_USER_NAME.getText().toString();

                String data = URLEncoder.encode("grant_type", "UTF-8") + "=" + URLEncoder.encode("password", "UTF-8") + "&" +
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(reg_user_name, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(reg_user_pass, "UTF-8");

                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                out.write(data.toString());
                out.close();

                int HttpResult = httpURLConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String line = null;

                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();

                    JSONObject myJson = new JSONObject(sb.toString());
                    r.response_expires_in = myJson.optString("expires_in");
                    r.response_access_token = myJson.optString("access_token");
                    r.error = null;
                } else {
                    System.out.println(httpURLConnection.getResponseMessage());
                    System.out.println(HttpResult);
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    JSONObject myJson = new JSONObject(sb.toString());
                    String error = myJson.optString("error");
                    r.error = error;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                r.error = e.getMessage();
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            return r;
        }

        protected void onPostExecute(Response r) {
            if (r.error != null) {
                Toast.makeText(getApplicationContext(), r.error, Toast.LENGTH_LONG).show();
            } else if (r.response_access_token != null && r.response_expires_in != null) {
                TokenModel tokenModel = AppUtils.decodeToken(r.response_access_token);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AppConstants.PREF_USER_ID, tokenModel.getUserId());
                editor.putString(AppConstants.PREF_ACCESS_TOKEN, r.response_access_token);
                editor.putLong(AppConstants.PREF_EXP, tokenModel.getExp());
                editor.apply();
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }

        public class Response {
            public String error;
            public String response_access_token;
            public String response_expires_in;
        }
    }
}
