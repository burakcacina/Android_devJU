package net.burak.androidproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/* This is Created
        by
      BURAK CACINA
*/

public class EditRecipeActivity extends AppCompatActivity {
    EditText ET_RECIPENAME,ET_DESCRIPTION,ET_DIRECTION,ET_DIRECTION_2;
    String access_token,USERID,recipe_name,recipe_directions,recipe_descriptions,recipe_directions2,URL,error;
    Uri selectedImage;
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
        ((Button)findViewById(R.id.upload_photo_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                clickpic();
            }
        });
    }

    private void clickpic() {
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // start the image capture Intent
        startActivityForResult(intent, 100);
    }

    public  void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e("onActivityResult", "Result code: " + requestCode);
        if (requestCode == 100 && resultCode == RESULT_OK) {

            selectedImage = intent.getData();

            Log.e("IMAGE PATH", getPath(getApplicationContext(), intent.getData()));

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int data = prefs.getInt("RECIPEID", 1); //no id: default value
            if (data == 1)
            {
                Log.e("EditRecipeActivity", "bad id");
            }

            //test();
            new uploadImageToServer().execute("http://52.211.99.140/api/v1/recipes/" + data + "/image");
        }
    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if ( cursor.moveToFirst( ) ) {
            int column_index = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
            result = cursor.getString( column_index );
        }
        cursor.close( );
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    private class uploadImageToServer extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String attachmentName = "image";
            String attachmentFileName = "the-filename.jpeg";
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary = "Some_random_text_that_cannot_be_repeated_randomly";

            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + access_token);
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                httpURLConnection.setRequestMethod("PUT");

                DataOutputStream request = new DataOutputStream(
                        httpURLConnection.getOutputStream());

                request.writeBytes(crlf);
                request.writeBytes(crlf);

                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"" +
                        attachmentName + "\";filename=\"" +
                        attachmentFileName + "\"" + crlf);
                request.writeBytes("Content-Type: image/jpeg" + crlf);
                request.writeBytes(crlf);

                String filepath = getPath(getApplicationContext(), selectedImage);
                File imagefile = new File(filepath);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(imagefile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bm = BitmapFactory.decodeStream(fis);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
                byte[] b = baos.toByteArray();
                Log.e("Image size", String.valueOf(b.length));

                request.write(b);

                request.writeBytes(crlf);
                request.writeBytes(twoHyphens + boundary +
                        twoHyphens + crlf);

                request.flush();
                request.close();

                InputStream responseStream = new
                        BufferedInputStream(httpURLConnection.getInputStream());

                BufferedReader responseStreamReader =
                        new BufferedReader(new InputStreamReader(responseStream));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();

                String response = stringBuilder.toString();

                responseStream.close();
                httpURLConnection.disconnect();


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

        protected void onPostExecute(String error) {
            if (error != null) {
                //Snackbar.make(((Activity)context).findViewById(R.id.relativeLayout2), error, Snackbar.LENGTH_SHORT).show();
            } else {
                //Snackbar.make(((Activity)context).findViewById(R.id.relativeLayout2), "Picture was uploaded successfuly", Snackbar.LENGTH_SHORT).show();
            }
        }
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
                URL url = new URL(params[0]);
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
                    System.out.println("EDITED");

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
