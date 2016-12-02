package net.burak.loginupdatesignup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.burak.loginupdatesignup.models.RecipeModel;

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

public class DetailActivity extends AppCompatActivity {

    int sa;
    private ListView lvRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);

        lvRecipes = (ListView) findViewById(R.id.lvRecipes);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String json = bundle.getString("recipeModel");
            RecipeModel recipeModel = new Gson().fromJson(json, RecipeModel.class);
            sa = recipeModel.getid();
        }
        final String url1 = "http://52.211.99.140/api/v1/recipes/" + sa;
        new JSONTask().execute(url1);
    }

    public class JSONTask extends AsyncTask<String, String, List<RecipeModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

                List<RecipeModel> recipeModelList = new ArrayList<>();
                Gson gson = new Gson();

                JSONObject finalObject = new JSONObject(sb.toString());
                JSONObject json = new JSONObject(sb.toString());
                JSONObject json2 = json.getJSONObject("creator");
                if (httpURLConnection.getResponseCode() == 200) {

                    RecipeModel recipeModel = gson.fromJson(finalObject.toString(), RecipeModel.class);
                    recipeModel.setTagline(finalObject.getString("name"));
                    recipeModel.setDescription(finalObject.getString("description"));
                    recipeModel.setImage(finalObject.getString("image"));
                    recipeModel.setUserName(json2.getString("userName"));
                    recipeModel.setUserid(json2.getString("id"));

                    List<RecipeModel.directions> directionsList = new ArrayList<>();
                    for (int j = 0; j < finalObject.getJSONArray("directions").length(); j++) {
                        RecipeModel.directions directions = new RecipeModel.directions();
                        directions.setDescription(finalObject.getJSONArray("directions").getJSONObject(j).getString("description"));
                        directions.setOrder(finalObject.getJSONArray("directions").getJSONObject(j).getInt("order"));

                        directionsList.add(directions);
                    }
                    recipeModel.setdirectionsList(directionsList);
                    recipeModelList.add(recipeModel);
                }
                return recipeModelList;

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
            RecipeAdapter adapter = new RecipeAdapter(getApplicationContext(), R.layout.activity_detail, result);
            lvRecipes.setAdapter(adapter);
            Button but1 = (Button) findViewById(R.id.deletebutton);
            Button but2 = (Button) findViewById(R.id.updatebutton);
            Button but3 = (Button) findViewById(R.id.editbutton);

            SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this);
            final String userID = prefs2.getString("USERID", "no id"); //no id: default value

            RecipeModel recipeModel = result.get(0);
            int recipeid = recipeModel.getid();

            SharedPreferences prefs3 = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this);
            SharedPreferences.Editor editor3 = prefs3.edit();
            editor3.putInt("RECIPEID", recipeid);
            editor3.commit();

            String sa = recipeModel.getUserid();
            if (userID.equals(sa)) {
                but1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        {
                            Intent intent = new Intent(DetailActivity.this, DeleteRecipeActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                but2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        {
                            Intent intent = new Intent(DetailActivity.this, UpdateRecipeActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                but3.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        {
                            Intent intent = new Intent(DetailActivity.this, EditRecipeActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Can't have access", Toast.LENGTH_LONG).show();
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
                holder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
                holder.tvDirections = (TextView) convertView.findViewById(R.id.tvDirections);
                holder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);

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

            holder.tvRecipeName.setText(recipeModelList.get(position).getTagline());
            holder.tvDescription.setText(recipeModelList.get(position).getDescription());
            holder.tvUsername.setText(recipeModelList.get(position).getUserName());

            StringBuffer stringBuffer = new StringBuffer();
            for (RecipeModel.directions directions : recipeModelList.get(position).getdirectionsList()) {
                stringBuffer.append(directions.getOrder() + ".  " + directions.getDescription() + " \n");
            }

            holder.tvDirections.setText("Directions" + "\n" + stringBuffer);

            return convertView;
        }


        class ViewHolder {
            private ImageView ivRecipeIcon;
            private TextView tvRecipeName;
            private TextView tvDescription;
            private TextView tvDirections;
            private TextView tvUsername;
        }
    }
}

