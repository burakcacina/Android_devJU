package net.burak.androidproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class SearchActivity extends AppCompatActivity {
    String URL;
    EditText ET_REG_USER_NAME;
    String reg_user_name;
    private ListView lvRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        lvRecipes = (ListView) findViewById(R.id.lvRecipes);
        Button but1 = (Button) findViewById(R.id.buttonSearch);

        ET_REG_USER_NAME = (EditText) findViewById(R.id.SearchItem);


        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                {
                    new JSONTask().execute(URL);
                }
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, List<RecipeModel>> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            reg_user_name = ET_REG_USER_NAME.getText().toString();
            String URL_TO_HIT = "http://52.211.99.140/api/v1/recipes/search?term=" + reg_user_name;
            URL = URL_TO_HIT;

        }

        @SuppressWarnings("WrongThread")
        @Override
        protected List<RecipeModel> doInBackground(String... params) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL(URL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Host", "11.12.21.22");
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
                    System.out.println(sb.toString());

                    List<RecipeModel> RecipeModelList = new ArrayList<>();

                    JSONArray parentArray = new JSONArray(sb.toString());
                    Gson gson = new Gson();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        RecipeModel recipeModel = gson.fromJson(finalObject.toString(), RecipeModel.class);
                        RecipeModelList.add(recipeModel);
                    }

                    return RecipeModelList;
                }
                else
                {
                    System.out.println("ERROROROR");

                }
            }
            catch (MalformedURLException e) {
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
            if(result != null) {
                RecipeAdapter adapter = new RecipeAdapter(getApplicationContext(), R.layout.activity_detail_search, result);
                lvRecipes.setAdapter(adapter);
                lvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        RecipeModel recipeModel = result.get(position);
                        Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                        intent.putExtra("recipeModel", new Gson().toJson(recipeModel));
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Write a keyword for search.", Toast.LENGTH_SHORT).show();
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
                holder.ivRecipeIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
                holder.tvRecipeName = (TextView)convertView.findViewById(R.id.tvRecipeName);
                holder.tvRecipeID = (TextView)convertView.findViewById(R.id.tvRecipeID);
                holder.tvDescription = (TextView)convertView.findViewById(R.id.tvDescription);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

            }
            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

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

            holder.tvRecipeName.setText(recipeModelList.get(position).getTagline());
            holder.tvRecipeID.setText("ID: " + recipeModelList.get(position).getid());
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

