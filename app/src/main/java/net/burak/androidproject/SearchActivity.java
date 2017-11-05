package net.burak.androidproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import net.burak.androidproject.helpers.RecipesHelper;
import net.burak.androidproject.models.RecipeModel;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText searchText;
    private String reg_user_name;
    private ListView lvRecipes;
    private RecipesHelper recipesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        this.recipesHelper = new RecipesHelper(this);

        lvRecipes = (ListView) findViewById(R.id.lvRecipes);
        Button searchBtn = (Button) findViewById(R.id.buttonSearch);

        searchText = (EditText) findViewById(R.id.SearchItem);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                {
                    new SearchRecipeTask().execute(searchText.getText().toString());
                }
            }
        });
    }

    public class SearchRecipeTask extends AsyncTask<String, String, List<RecipeModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<RecipeModel> doInBackground(String... params) {
            return recipesHelper.searchRecipesInServer(params[0]);
        }

        @Override

        protected void onPostExecute(final List<RecipeModel> result) {
            super.onPostExecute(result);
            if (result != null) {
                RecipeAdapter adapter = new RecipeAdapter(getApplicationContext(), R.layout.activity_detail_search, result);
                lvRecipes.setAdapter(adapter);
                lvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        RecipeModel recipeModel = result.get(position);
                        Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                        intent.putExtra(AppConstants.RECIPE_ID, ""+recipeModel.getId());
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
                holder.ivRecipeIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
                holder.tvRecipeName = (TextView) convertView.findViewById(R.id.tvRecipeName);
                holder.tvRecipeID = (TextView) convertView.findViewById(R.id.tvRecipeID);
                holder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);

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

