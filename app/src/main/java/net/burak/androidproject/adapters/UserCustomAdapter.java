package net.burak.androidproject.adapters;

/**
 * Created by Cube on 2/6/2017.
 */

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
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.burak.androidproject.CommentActivity;
import net.burak.androidproject.R;
import net.burak.androidproject.models.CommentModel;

import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

public class UserCustomAdapter extends ArrayAdapter<CommentModel> {

    static Context context;
    int layoutResourceId;
    ArrayList<CommentModel> data = new ArrayList<CommentModel>();
    static String error;
    static private String access_token;
    private View view;
    private static final int RESULT_LOAD_IMAGE = 1;
    static String response;

    // for testing purposes


    static Uri selectedImage;
    private static String commentURL = "http://52.211.99.140/api/v1/comments/";
    // for testing purposes

    public UserCustomAdapter(Context context, int layoutResourceId,
                             ArrayList<CommentModel> data, String access_token) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.access_token = access_token;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View row = convertView;
        UserHolder holder = null;

        view = parent;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new UserHolder();
            holder.textName = (TextView) row.findViewById(R.id.textView1);
            holder.textGrade = (TextView) row.findViewById(R.id.textView2);
            holder.textUsername = (TextView) row.findViewById(R.id.textView3);
            holder.btnEdit = (Button) row.findViewById(R.id.button1);
            holder.btnDelete = (Button) row.findViewById(R.id.button2);
            holder.btnUpload = (Button) row.findViewById(R.id.button3);
            holder.image = (ImageView) row.findViewById(R.id.comment_image);
            row.setTag(holder);
        } else {
            holder = (UserHolder) row.getTag();
        }
        final CommentModel comment = data.get(position);
        holder.textName.setText(comment.getText());
        holder.textGrade.setText("Grade: " + String.valueOf(comment.getGrade()));
        holder.textUsername.setText(comment.getCommenter().getUserName());
        if (!comment.getCommenter().getId().equals(getCommenterId()))
        {
            holder.btnEdit.setVisibility(View.INVISIBLE);
            holder.btnDelete.setVisibility(View.INVISIBLE);
            holder.btnUpload.setVisibility(View.INVISIBLE);
        }

        //holder.image.setImageBitmap(getBitmapFromURL(comment.getImage()));
        ImageLoader.getInstance().displayImage(comment.getImage(), holder.image);

        holder.btnEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("EDIT COMMENT");

                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                final RatingBar ratingBar = new RatingBar(getContext());
                ratingBar.setLayoutParams(new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT));
                ratingBar.setStepSize(1);
                ratingBar.setRating(comment.getGrade());

                // Set up the input
                final EditText input = new EditText(getContext());
                input.setText(comment.getText());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                layout.addView(ratingBar);
                layout.addView(input);
                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.e("Comment id", String.valueOf(comment.getid()));
                        Snackbar.make(parent, "Comment has been updated", Snackbar.LENGTH_SHORT).show();
                                                                                            //params 0 1 and 2
                        new JSONTaskEdit().execute("http://52.211.99.140/api/v1/comments/" + comment.getid(), access_token, input.getText().toString(), String.valueOf((int)ratingBar.getRating()));
                        ((Activity)context).finish();
                        getContext().startActivity(new Intent(getContext(), CommentActivity.class));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        holder.btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONTaskDelete().execute("http://52.211.99.140/api/v1/comments/" + String.valueOf(comment.getid()), access_token);
                ((Activity)context).finish();
                getContext().startActivity(new Intent(getContext(), CommentActivity.class));
            }
        });
        holder.btnUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickpic();
                commentURL += comment.getid() + "/image";
                Log.i("INFO", commentURL);
                //CommentActivity.URL = commentURL;
            }
        });
        return row;

    }

    private void clickpic() {
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // start the image capture Intent
        ((Activity)context).startActivityForResult(intent, 100);
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", "Result code: " + requestCode);
        if (requestCode == 100 && resultCode == RESULT_OK) {

            selectedImage = data.getData();
            Log.e("IMAGE PATH", getPath(context, data.getData()));
            new uploadImageToServer().execute();
        }
    }

    public static String getPath( Context context, Uri uri ) {
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

    private static class uploadImageToServer extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

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
                URL url = new URL(commentURL);
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

                String filepath = getPath(context, selectedImage);
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
                Snackbar.make(((Activity)context).findViewById(R.id.relativeLayout2), error, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(((Activity)context).findViewById(R.id.relativeLayout2), "Picture was uploaded successfuly", Snackbar.LENGTH_SHORT).show();
                ((Activity)context).finish();
            }
        }
    }

    private String getCommenterId()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String iduser = prefs.getString("USERID", "no id");
        return iduser;
    }

    private static class UserHolder {
        TextView textName;
        TextView textGrade;
        TextView textUsername;
        Button btnEdit;
        Button btnDelete;
        Button btnUpload;
        ImageView image;
    }

    private class JSONTaskDelete extends AsyncTask<String, Void, String> {

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

            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + params[1]);
                httpURLConnection.setRequestMethod("DELETE");
                httpURLConnection.connect();

                int HttpResult = httpURLConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_CREATED) {
                    System.out.println("created");

                } /*else {
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
                }*/
            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            /*
            catch (JSONException e) {
                e.printStackTrace();
            }*/
            finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            return null;
        }

        protected void onPostExecute(String error) {
            if (error != null) {
                Snackbar.make(view, error, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(view, "Comment has been deleted", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private class JSONTaskEdit extends AsyncTask<String, Void, String> {

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
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + params[1]);
                httpURLConnection.setRequestMethod("PATCH");
                httpURLConnection.connect();

                JSONObject jsonParam = new JSONObject();
                 /*JSONObject commenter = new JSONObject();
                commenter.put("id", USERID);
                commenter.put("userName", USERID);*/
                jsonParam.put("text", params[2]);
                jsonParam.put("grade", params[3]);
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

                }
                else if (HttpResult == HttpURLConnection.HTTP_ACCEPTED)
                {
                    System.out.println("Accepted");
                }
                else {
                    /*BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"));
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
                    return error;*/
                }
            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            return null;
        }

        protected void onPostExecute(String error) {
            if (error != null) {
                Snackbar.make(view, error, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}