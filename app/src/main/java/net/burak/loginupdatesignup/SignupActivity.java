package net.burak.loginupdatesignup;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignupActivity extends BaseActivity {

    private final String URL_TO_HIT = "http://52.211.99.140/api/v1/tokens/password";
    EditText ET_LATITUTE,ET_LONGITUTE,ET_USER_NAME,ET_USER_PASS;
    String user_name,user_pass,latitute,longitute;
    LoginDataBaseAdapter loginDataBaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
super.showAdvertisement();
        Button but1 = (Button) findViewById(R.id.userReg);
        ET_USER_NAME= (EditText)findViewById(R.id.new_user_name);
        ET_USER_PASS = (EditText)findViewById(R.id.new_user_pass);
        ET_LATITUTE = (EditText)findViewById(R.id.Latitute);
        ET_LONGITUTE = (EditText)findViewById(R.id.Longitute);

        // To start fetching the data when app start, uncomment below line to start the async task.
        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();

        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SignupActivity.JSONTask().execute(URL_TO_HIT);
            }
        });
    }

    public class JSONTask extends AsyncTask<String, Void, JSONTask.Response> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Response doInBackground(String... params) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String reg_url = "http://52.211.99.140/api/v1/accounts/password";
            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL(reg_url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect(); //Create JSONObject here JSONObject
                JSONObject jsonParam = new JSONObject();

                user_pass =ET_USER_PASS.getText().toString();
                user_name =ET_USER_NAME.getText().toString();
                latitute  =ET_LATITUTE.getText().toString();
                longitute =ET_LONGITUTE.getText().toString();

                jsonParam.put("userName",user_name);
                jsonParam.put("password", user_pass);
                jsonParam.put("latitude", latitute);
                jsonParam.put("longitude", longitute);

                Response r = new Response();

                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                out.write(jsonParam.toString());
                out.close();

                int HttpResult = httpURLConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            httpURLConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    JSONObject myJson = new JSONObject(sb.toString());
                    r.response_id = myJson.optString("id");
                    r.response_error=null;
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    intent.putExtra("id", r.response_id);
                    startActivity(intent);
                    return r;
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    JSONObject jsonObj = new JSONObject(sb.toString());
                    JSONArray arrayJson2 = jsonObj.getJSONArray("errors");

                    for (int i = 0; i < arrayJson2.length(); i++) {
                        r.response_error = arrayJson2.getString(i);
                        return r;
                    }
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
            public String response_error;
            public String response_id;
        }

        protected void onPostExecute(Response r) {
            if (r.response_error != null && r.response_id == null) {
                Toast.makeText(getApplicationContext(), r.response_error, Toast.LENGTH_LONG).show();
            }
            else if(r.response_id != null && r.response_error == null) {
                Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Redirecting Login Screen", Toast.LENGTH_LONG).show();
            }
            loginDataBaseAdapter.insertEntry(user_name, r.response_id);
        }
    }
}
