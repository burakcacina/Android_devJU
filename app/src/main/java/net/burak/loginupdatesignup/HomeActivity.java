package net.burak.loginupdatesignup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.burak.loginupdatesignup.models.RecipeModel;

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

public class HomeActivity extends AppCompatActivity {

    private ListView lvRecipes;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(this);
        String dataa = prefs2.getString("USERID", "no id"); //no id: default value
        System.out.println(dataa);

        SharedPreferences prefs3 = PreferenceManager.getDefaultSharedPreferences(this);
        String data = prefs3.getString("access_token", "no id"); //no id: default value
        System.out.println(data);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start

        lvRecipes = (ListView)findViewById(R.id.lvRecipes);

        Button but1 = (Button) findViewById(R.id.page1);
        Button but2 = (Button) findViewById(R.id.page2);
        Button but3 = (Button) findViewById(R.id.page3);
        Button but4 = (Button) findViewById(R.id.page4);
        Button but5 = (Button) findViewById(R.id.page5);
        Button but6 = (Button) findViewById(R.id.createRecipe);

        String URL_TO_HIT = "http://52.211.99.140/api/v1/recipes?page=1";
        new JSONTask().execute(URL_TO_HIT);

        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                {
                    String URL_TO_HIT = "http://52.211.99.140/api/v1/recipes?page=1";
                    new JSONTask().execute(URL_TO_HIT);
                }
            }
        });
        but2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                {
                    String URL_TO_HIT = "http://52.211.99.140/api/v1/recipes?page=2";
                    new JSONTask().execute(URL_TO_HIT);
                }
            }
        });
        but3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                {
                    String URL_TO_HIT = "http://52.211.99.140/api/v1/recipes?page=3";
                    new JSONTask().execute(URL_TO_HIT);
                }
            }
        });
        but4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                {
                    String URL_TO_HIT = "http://52.211.99.140/api/v1/recipes?page=4";
                    new JSONTask().execute(URL_TO_HIT);
                }
            }
        });
        but5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                {
                    String URL_TO_HIT = "http://52.211.99.140/api/v1/recipes?page=9";
                    new JSONTask().execute(URL_TO_HIT);
                }
            }
        });
        but6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                {
                    Intent intent = new Intent(HomeActivity.this, CreateRecipeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public class JSONTask extends AsyncTask<String,String, List<RecipeModel> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<RecipeModel> doInBackground(String... params) {
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

                List<RecipeModel> RecipeModelList = new ArrayList<>();

                JSONArray parentArray = new JSONArray(sb.toString());
                Gson gson = new Gson();
                for(int i=0; i<parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    RecipeModel recipeModel = gson.fromJson(finalObject.toString(), RecipeModel.class);
                    RecipeModelList.add(recipeModel);
                }

                return RecipeModelList;

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
            return  null;
        }

        @Override
        protected void onPostExecute(final List<RecipeModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                RecipeAdapter adapter = new RecipeAdapter(getApplicationContext(), R.layout.row, result);
                lvRecipes.setAdapter(adapter);
                lvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        RecipeModel recipeModel = result.get(position);
                        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                        intent.putExtra("recipeModel", new Gson().toJson(recipeModel));
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class RecipeAdapter extends ArrayAdapter {

        private List<RecipeModel> RecipeModelList;
        private int resource;
        private LayoutInflater inflater;
        public RecipeAdapter(Context context, int resource, List<RecipeModel> objects) {
            super(context, resource, objects);
            RecipeModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivRecipeIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
                holder.tvRecipeName = (TextView)convertView.findViewById(R.id.tvRecipeName);
                holder.tvRecipeID = (TextView)convertView.findViewById(R.id.tvRecipeID);
                holder.tvCreated = (TextView)convertView.findViewById(R.id.tvCreated);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);

            ImageLoader.getInstance().displayImage(RecipeModelList.get(position).getImage(), holder.ivRecipeIcon, new ImageLoadingListener() {
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

            holder.tvRecipeName.setText(RecipeModelList.get(position).getTagline());
            holder.tvRecipeID.setText("ID: " + RecipeModelList.get(position).getid());
            holder.tvCreated.setText("Created: " + RecipeModelList.get(position).getCreated());
            return convertView;
        }


        class ViewHolder{
            private ImageView ivRecipeIcon;
            private TextView tvRecipeName;
            private TextView tvRecipeID;
            private TextView tvCreated;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_update) {
            Intent intentUpdate = new Intent(getApplicationContext(), UpdateAccActivity.class);
            startActivity(intentUpdate);
        }
        else if (item.getItemId() == R.id.action_exit) {
                this.finishAffinity();
        }
        return true;
    }










}

