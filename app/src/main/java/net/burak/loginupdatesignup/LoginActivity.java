package net.burak.loginupdatesignup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends BaseActivity {

    LoginDataBaseAdapter loginDataBaseAdapter;
    private final String URL_TO_HIT = "http://52.211.99.140/api/v1/tokens/password";
    EditText ET_REG_USER_NAME, ET_REG_USER_PASS;
    String reg_user_name, reg_user_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        super.showAdvertisement();
        //google admob
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6611688605030855/6976556923");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ET_REG_USER_NAME = (EditText) findViewById(R.id.reg_user_name);
        ET_REG_USER_PASS = (EditText) findViewById(R.id.reg_user_pass);
        Button but2 = (Button) findViewById(R.id.userLog);

        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();

        // To start fetching the data when app start, uncomment below line to start the async task.
        but2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new LoginActivity.JSONTask().execute(URL_TO_HIT);

            }
        });        // To start fetching the data when app start, uncomment below line to start the async task.
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
                URL url = new URL(URL_TO_HIT);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
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

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("access_token", r.response_access_token);
                    startActivity(intent);
                    return r;

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
                    return r;
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

        public class Response {
            public String error;
            public String response_access_token;
            public String response_expires_in;
        }

        protected void onPostExecute(Response r) {
            if (r.error != null) {
                Toast.makeText(getApplicationContext(), r.error, Toast.LENGTH_LONG).show();

            } else if (r.response_access_token != null && r.response_expires_in != null) {
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("access_token", r.response_access_token);
            editor.commit();

            String storedPassword=loginDataBaseAdapter.getSinlgeEntry(reg_user_name);
            SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor editor2 = prefs.edit();
            editor2.putString("USERID", storedPassword);
            editor2.commit();
        }
    }
}
