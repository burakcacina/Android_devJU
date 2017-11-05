package net.burak.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import net.burak.androidproject.models.RecipeModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static net.burak.androidproject.AppConstants.PREFS;

public class UsersRecipeAndComments extends AppCompatActivity {

    private ListView lvRecipes;
    private String userId, accessToken;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercreated);

        this.sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.userId = this.sharedPreferences.getString(AppConstants.PREF_USER_ID, null);
        this.accessToken = this.sharedPreferences.getString(AppConstants.PREF_ACCESS_TOKEN, null);

        lvRecipes = (ListView) findViewById(R.id.lvRecipes);
        Button but1 = (Button) findViewById(R.id.userRecipesbutton);
        Button but2 = (Button) findViewById(R.id.userCommentbutton);

        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                {
                    final String URL_TO_HIT = "http://52.211.99.140/api/v1/accounts/" + userId + "/recipes";
                    new JSONTask().execute(URL_TO_HIT);
                }
            }
        });

        but2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                {
                    final String URL_TO_HIT = "http://52.211.99.140/api/v1/accounts/" + userId + "/comments";
                    new JSONTask().execute(URL_TO_HIT);
                }
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, List<RecipeModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected List<RecipeModel> doInBackground(String... params) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Host", AppConstants.MJILIK_HOST_ENDPOINT);
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String line = null;

                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                br.close();

                if (httpURLConnection.getResponseCode() == 200) {

                    List<RecipeModel> RecipeModelList = new ArrayList<>();

                    JSONArray parentArray = new JSONArray(sb.toString());
                    Gson gson = new Gson();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        RecipeModel recipeModel = gson.fromJson(finalObject.toString(), RecipeModel.class);
                        RecipeModelList.add(recipeModel);
                    }

                    return RecipeModelList;
                } else {
                    System.out.println("ERROROROR");

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

        @Override

        protected void onPostExecute(final List<RecipeModel> result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    RecipeAdapter adapter = new RecipeAdapter(getApplicationContext(), R.layout.activity_detail_search, result);
                    lvRecipes.setAdapter(adapter);
                    lvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            RecipeModel recipeModel = result.get(position);
                            Intent intent = new Intent(UsersRecipeAndComments.this, DetailActivity.class);
                            intent.putExtra("recipeModel", new Gson().toJson(recipeModel));
                            startActivity(intent);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Write a keyword for search.", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class RecipeAdapter extends ArrayAdapter {

        private List<RecipeModel> recipeModelList;
        private int resource;
        private LayoutInflater inflater;

        public RecipeAdapter(Context context, int resource, List<RecipeModel> objects) {
            super(context, resource, objects);
            recipeModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivRecipeIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
                holder.tvRecipeName = (TextView) convertView.findViewById(R.id.tvRecipeName);
                holder.tvRecipeID = (TextView) convertView.findViewById(R.id.tvRecipeID);
                holder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

            }
            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            // Then later, when you want to display image
            ImageLoader.getInstance().displayImage(recipeModelList.get(position).getImage(), holder.ivRecipeIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });

            // Then later, when you want to display image

            holder.tvRecipeName.setText(recipeModelList.get(position).getName());
            holder.tvRecipeID.setText("ID: " + recipeModelList.get(position).getId());
            holder.tvDescription.setText(recipeModelList.get(position).getDescription());


            return convertView;
        }


        class ViewHolder {
            private ImageView ivRecipeIcon;
            private TextView tvRecipeName;
            private TextView tvRecipeID;
            private TextView tvDescription;
        }
    }
}
