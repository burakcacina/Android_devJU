package net.burak.androidproject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.Gson;

import net.burak.androidproject.adapters.UserCustomAdapter;
import net.burak.androidproject.models.CommentModel;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cube on 2/5/2017.
 */

public class CommentActivity extends AppCompatActivity {

    EditText commentBox;
    private String URL_TO_HIT = "http://52.211.99.140/api/v1/recipes";
    private String access_token, USERID, commmet_text, recipe_id, error;
    private static final int RESULT_LOAD_IMAGE = 1;
    //Uri selectedImage = null;
    final private int MY_PERMISSIONS_REQUEST_READ_STORAGE = 123;

    //testing data
    Button btpic, btnup;
    private Uri fileUri;
    String picturePath;
    Uri selectedImage;
    Bitmap photo;
    String ba1;
    public static String URL = "Paste your URL here";
    // testing data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //Asking for permissions

        if (ContextCompat.checkSelfPermission(CommentActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(CommentActivity.this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(CommentActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //Needed for Specification
        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs2.getString("access_token", "no id");
        access_token = token;

        //Need for Creator ID while creating recipe
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String iduser = prefs.getString("USERID", "no id");
        USERID = iduser;

        commentBox = (EditText) findViewById(R.id.commentText);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int recipieId = sharedPreferences.getInt("RECIPEID", 0);

        URL_TO_HIT += "/" + String.valueOf(recipieId) + "/comments";

        Button button = (Button) findViewById(R.id.postButton1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONTask().execute(URL_TO_HIT);
                finish();
                startActivity(new Intent(getApplication(), CommentActivity.class));
            }
        });

        new JSONTask2().execute("http://52.211.99.140/api/v1/recipes/" + recipieId + "/comments");


    }

    private void clickpic() {
        // Check Camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Open default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, 100);

        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {

            selectedImage = data.getData();
            photo = (Bitmap) data.getExtras().get("data");

            // Cursor to get image uri to display

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //ImageView imageView = (ImageView) findViewById(R.id.Imageprev);
            //imageView.setImageBitmap(photo);
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UserCustomAdapter.onActivityResult(requestCode, resultCode, data);

    }

    private void upload() {
        // Image location URL
        Log.e("path", "----------------" + picturePath);

        // Image
        Bitmap bm = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        //ba1 = Base64.encodeBytes(ba);

        Log.e("base64", "-----" + ba1);

        // Upload image to server
        //new uploadToServer().execute();

    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null)
        {
            selectedImage = data.getData();
            Snackbar.make(findViewById(R.id.relativeLayout2), selectedImage.toString(), Snackbar.LENGTH_SHORT).show();
        }
    }*/



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
                URL url = new URL(URL_TO_HIT);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + access_token);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();
                JSONObject jsonParam = new JSONObject();

                commmet_text = commentBox.getText().toString();

                jsonParam.put("commenterId", USERID);
                jsonParam.put("text", commmet_text);
                jsonParam.put("grade", (int)((RatingBar)findViewById(R.id.ratingBar)).getRating());

                //jsonParam.put("recipe_id", recipe_id);

                /*System.out.println(access_token);
                System.out.println(USERID);
                System.out.println(commmet_text);
                System.out.println(URL_TO_HIT);*/

                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                out.write(jsonParam.toString());
                out.close();

                int HttpResult = httpURLConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_CREATED) {
                    System.out.println("created");

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
                //Snackbar.make(findViewById(R.id.relativeLayout2), error, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(findViewById(R.id.relativeLayout2), "Comment has been posted successfuly", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
    public class JSONTask2 extends AsyncTask<String, String, List<CommentModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog.show();
        }

        @Override
        protected List<CommentModel> doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String line = null;

                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                br.close();

                List<CommentModel> commentModelList = new ArrayList<>();
                Gson gson = new Gson();
                JSONArray jsonArray = new JSONArray(sb.toString());

                if(httpURLConnection.getResponseCode() == 200) {
                    for (int counter = 0; counter < jsonArray.length(); counter++)
                    {
                        commentModelList.add(gson.fromJson(jsonArray.get(counter).toString(), CommentModel.class));
                    }
                }
                return commentModelList;

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

        @Override
        protected void onPostExecute(final List<CommentModel> result) {
            super.onPostExecute(result);
            // Generating list
            ArrayList<String> comments = new ArrayList<>();
            for (CommentModel one : result)
            {
                comments.add(one.getText());
                Log.e("Comment ids", String.valueOf(one.getid()));
            }

            ArrayList<CommentModel> test = new ArrayList<CommentModel>();
            for (CommentModel one : result)
            {
                test.add(one);
            }

            UserCustomAdapter userAdapter = new UserCustomAdapter(CommentActivity.this, R.layout.test_layout_adapter, test, access_token);
            ListView lView = (ListView)findViewById(R.id.commentsListView);
            lView.setItemsCanFocus(false);
            lView.setAdapter(userAdapter);
            /**
             * get on item click listener
             */
            /*lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View v,
                                        final int position, long id) {
                    Log.i("List View Clicked", "**********");
                    Toast.makeText(CommentActivity.this,
                            "List View Clicked:" + position, Toast.LENGTH_LONG)
                            .show();
                }
            });*/
        }
        public String getPath(Uri uri)
        {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            if (cursor == null) return null;
            int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String s=cursor.getString(column_index);
            cursor.close();
            return s;
        }
        /**/

        /*public class JSONTaskUpload extends AsyncTask<String, String, List<CommentModel>> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<CommentModel> doInBackground(String... params) {
                StringBuilder sb = new StringBuilder();
                HttpURLConnection httpURLConnection = null;
                try {
                    URL url = new URL(params[0]);
                    httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=SOME_BOUNDARY");
                    httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                    httpURLConnection.setRequestProperty("Authorization", "Bearer " + access_token);
                    httpURLConnection.setRequestMethod("PUT");
                    httpURLConnection.connect();

                    JSONObject jsonParam = new JSONObject();

                    jsonParam.put("commenterId", USERID);
                    jsonParam.put("text", commmet_text);
                    jsonParam.put("grade", 3);



                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String line = null;

                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();

                    List<CommentModel> commentModelList = new ArrayList<>();
                    Gson gson = new Gson();
                    JSONArray jsonArray = new JSONArray(sb.toString());

                    if (httpURLConnection.getResponseCode() == 200) {
                        for (int counter = 0; counter < jsonArray.length(); counter++) {
                            commentModelList.add(gson.fromJson(jsonArray.get(counter).toString(), CommentModel.class));
                        }
                    }
                    return commentModelList;

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


                // TODO testing
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://example.com:1001/UPLOAD/FileUpload.do");
                File file = new File(pathFile);
                FileBody fileBody = new FileBody(file);

                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart("event", new StringBody("Upload"));
                reqEntity.addPart("type", new StringBody("invoice"));
                reqEntity.addPart("UploadedFile", fileBody);

                httpPost.setEntity(reqEntity);
                httpClient.execute(httpPost);
                // TODO testing
            }

            @Override
            protected void onPostExecute(final List<CommentModel> result) {
                super.onPostExecute(result);
                // Generating list
                ArrayList<String> comments = new ArrayList<>();
                for (CommentModel one : result) {
                    comments.add(one.getText());
                }

                ArrayList<CommentModel> test = new ArrayList<CommentModel>();
                for (CommentModel one : result) {
                    test.add(one);
                }

                UserCustomAdapter userAdapter = new UserCustomAdapter(CommentActivity.this, R.layout.test_layout_adapter, test, access_token);
                ListView lView = (ListView) findViewById(R.id.commentsListView);
                lView.setItemsCanFocus(false);
                lView.setAdapter(userAdapter);
            }
        }/**/
    }
}
