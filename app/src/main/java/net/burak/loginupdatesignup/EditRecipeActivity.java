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

public class EditRecipeActivity extends AppCompatActivity {
    EditText ET_RECIPENAME,ET_DESCRIPTION,ET_DIRECTION,ET_DIRECTION_2;
    String access_token,USERID,recipe_name,recipe_directions,recipe_descriptions,recipe_directions2,URL,error;
    int l=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        Button but1 = (Button) findViewById(R.id.edit_recipe_button);
        ET_RECIPENAME= (EditText)findViewById(R.id.edit_recipe_name);
        ET_DESCRIPTION = (EditText)findViewById(R.id.edit_recipe_description);
        ET_DIRECTION = (EditText)findViewById(R.id.edit_recipe_directions);
        ET_DIRECTION_2 = (EditText)findViewById(R.id.edit_recipe_directions2);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int data = prefs.getInt("RECIPEID", 1); //no id: default value

        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs2.getString("access_token", "no id"); //no id: default value
        access_token = token;

        final String URL_TO_HIT = "http://52.211.99.140/api/v1/recipes/" + data;

        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new JSONTask().execute(URL_TO_HIT);

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

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;

            try {
                java.net.URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + access_token);
                httpURLConnection.setRequestMethod("PATCH");
                httpURLConnection.connect();
                JSONObject jsonParam = new JSONObject();

                recipe_name =ET_RECIPENAME.getText().toString();
                recipe_descriptions =ET_DESCRIPTION.getText().toString();
                recipe_directions = ET_DIRECTION.getText().toString();
                recipe_directions2  =ET_DIRECTION_2.getText().toString();

                JSONArray arrForB = new JSONArray();
                for(l=1; l<3; l++) {
                    if (l == 1) {
                        JSONObject itemB = new JSONObject();
                        itemB.put("order", l);
                        itemB.put("description", recipe_directions);
                        arrForB.put(itemB);
                    }
                    if(l == 2) {
                        JSONObject itemB = new JSONObject();
                        itemB.put("order", l);
                        itemB.put("description", recipe_directions2);
                        arrForB.put(itemB);
                    }
                }
                jsonParam.put("name",recipe_name);
                jsonParam.put("description", recipe_descriptions);
                jsonParam.put("creatorId",USERID);
                jsonParam.put("directions", arrForB);

                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                out.write(jsonParam.toString());
                out.close();

                int HttpResult = httpURLConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {
                    System.out.println("CREATED");

                } else {
                    System.out.println(httpURLConnection.getResponseCode());
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    JSONArray arrayJson2 = jsonObj.getJSONArray("errors");

                    for (int i = 0; i < arrayJson2.length(); i++) {
                        error = arrayJson2.getString(i);

                    }
                    return error;
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

        protected void onPostExecute(String error) {
            if (error != null) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent = new Intent(EditRecipeActivity.this, HomeActivity.class);
                Toast.makeText(getApplicationContext(), "Recipe EDITED", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        }
    }
}
