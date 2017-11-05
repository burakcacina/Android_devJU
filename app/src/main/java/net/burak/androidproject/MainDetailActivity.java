package net.burak.androidproject;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import net.burak.androidproject.helpers.RecipesHelper;
import net.burak.androidproject.models.DirectionsModel;
import net.burak.androidproject.models.RecipeModel;


/* This is Created
        by
      BURAK CACINA
*/


public class MainDetailActivity extends AppCompatActivity {

    private int recipeID;
    private ProgressDialog dialog;
    private RecipesHelper recipesHelper;

    private ImageView ivRecipeIcon;
    private ProgressBar progressBar;
    private TextView tvRecipeName, tvDescription, tvDirections, tvUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recipe);

        this.recipesHelper = new RecipesHelper(this);

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
        ImageLoader.getInstance().init(config);

        ivRecipeIcon = (ImageView) findViewById(R.id.ivIcon);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvRecipeName = (TextView) findViewById(R.id.tvRecipeName);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvDirections = (TextView) findViewById(R.id.tvDirections);
        tvUsername = (TextView) findViewById(R.id.tvUsername);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipeID = Integer.parseInt(bundle.getString(AppConstants.RECIPE_ID));
        }
        new GetRecipeTask().execute();
    }

    public class GetRecipeTask extends AsyncTask<String, String, RecipeModel> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();

        }

        @Override
        protected RecipeModel doInBackground(String... params) {
            return recipesHelper.getRecipeById(recipeID);
        }

        @Override
        protected void onPostExecute(final RecipeModel recipe) {
            super.onPostExecute(recipe);
            if (recipe != null) {
                dialog.dismiss();
                if (recipe.getImage() != null && !recipe.getImage().equals("null")) {
                    ImageLoader.getInstance().displayImage(recipe.getImage(), ivRecipeIcon, new ImageLoadingListener() {
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
                }
                tvRecipeName.setText(recipe.getName());
                if (recipe.getDescription() != null) {
                    tvDescription.setText("Description: " + "\n" + recipe.getDescription());
                }
                if (recipe.getCreator() != null) {
                    tvUsername.setText("Name: " + recipe.getCreator().getUserName());
                }
                if (recipe.getDirections() != null) {
                    StringBuilder directions = new StringBuilder();
                    for (DirectionsModel direction : recipe.getDirections()) {
                        directions.append(direction.getOrder()).append(".  ").append(direction.getDescription()).append(" \n");
                    }
                    tvDirections.setText("Directions:" + "\n" + directions);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Couldn't fetch results", Toast.LENGTH_LONG).show();
            }
        }
    }
}
