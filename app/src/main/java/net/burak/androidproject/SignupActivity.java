package net.burak.androidproject;


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


/* This is Created
        by
      BURAK CACINA
*/


public class SignupActivity extends AppCompatActivity {

    private final String URL_TO_HIT = "http://52.211.99.140/api/v1/tokens/password";
    EditText ET_USER_NAME,ET_USER_PASS,ET_REG_USER_PASS2;
    String user_name,user_pass,reg_user_pass_confirmation;
    LoginDataBaseAdapter loginDataBaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button but1 = (Button) findViewById(R.id.userReg);
        ET_USER_NAME= (EditText)findViewById(R.id.new_user_name);
        ET_USER_PASS = (EditText)findViewById(R.id.new_user_pass);
        ET_REG_USER_PASS2 = (EditText) findViewById(R.id.reg_user_pass_confirmation);

        //USED FOR TO INSERT USERNAME AND ID TO THE DATABASE
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

                reg_user_pass_confirmation = ET_REG_USER_PASS2.getText().toString();

                Response r = new Response();


                if (!user_pass.equals(reg_user_pass_confirmation)) {
                    System.out.println("PASSWORD NOT EQUAL");
                    r.response_notmatch = "not match";
                }
                else{
                    jsonParam.put("password", user_pass);

                }

                jsonParam.put("userName",user_name);
                jsonParam.put("latitude", 0);
                jsonParam.put("longitude", 0);


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
                    System.out.println(sb.toString());
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
            public String response_notmatch;
            public String response_id;
        }

        protected void onPostExecute(Response r) {
            if (r.response_notmatch != null) {
                Toast.makeText(getApplicationContext(), "Password's not equal", Toast.LENGTH_LONG).show();
            }
            else if(r.response_error != null && r.response_id == null) {
                Toast.makeText(getApplicationContext(), r.response_error, Toast.LENGTH_LONG).show();
            }
            else if(r.response_id != null && r.response_error == null) {
                Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Redirecting Login Screen", Toast.LENGTH_LONG).show();
                loginDataBaseAdapter.insertEntry(user_name, r.response_id); //INSERTING ID AND NAME USING LOGINDATABASE CLASS
            }
        }
    }
}
