package net.burak.androidproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import net.burak.androidproject.helpers.RecipesHelper;
import net.burak.androidproject.models.DirectionsModel;
import net.burak.androidproject.models.RecipeModel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static net.burak.androidproject.AppConstants.PREFS;

/* This is Created
        by
      BURAK CACINA
*/

public class DetailActivity extends AppCompatActivity {

    private int recipeID;
    private ProgressDialog dialog;
    private RecipesHelper recipesHelper;

    private ImageView ivRecipeIcon;
    private ProgressBar progressBar;
    private TextView tvRecipeName, tvDescription, tvDirections, tvUsername;
    private ImageButton favoriteBtn;

    private String userId, accessToken;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recipe_home);

        this.recipesHelper = new RecipesHelper(this);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        this.sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.userId = this.sharedPreferences.getString(AppConstants.PREF_USER_ID, null);
        this.accessToken = this.sharedPreferences.getString(AppConstants.PREF_ACCESS_TOKEN, null);

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

        favoriteBtn = (ImageButton) findViewById(R.id.favoriteBtn);


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
            RecipeModel recipeModel = recipesHelper.getRecipeById(recipeID);
            List<RecipeModel> userFavRecipes = recipesHelper.getUserRecipeFav(userId, accessToken);
            if (userFavRecipes != null) {
                for (RecipeModel userFavRecipe : userFavRecipes) {
                    if (userFavRecipe.getId().equals(recipeModel.getId())) {
                        recipeModel.setFav(true);
                        break;
                    }
                }
            }
            return recipeModel;
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
                if (recipe.isFav())
                    favoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_favorite_black_24dp));

                Button delBtn = (Button) findViewById(R.id.deletebutton);
                Button editBtn = (Button) findViewById(R.id.editbutton);
                Button commentBtn = (Button) findViewById(R.id.go_comment_activity);

                String recipeCreator = null;
                if (recipe.getCreator() != null)
                    recipeCreator = recipe.getCreator().getId();

                if (recipeCreator != null && userId.equals(recipeCreator)) {
                    delBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            new DeleteRecipeTask().execute();
                        }
                    });
                    editBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            {
                                Intent intent = new Intent(DetailActivity.this, EditRecipeActivity.class);
                                intent.putExtra(AppConstants.RECIPE_ID, recipeID);
                                startActivity(intent);
                            }
                        }
                    });
                } else {
                    delBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            {
                                Toast.makeText(getApplicationContext(), "Can't have access to delete", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                    editBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            {
                                Toast.makeText(getApplicationContext(), "Can't have access to edit", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
                commentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DetailActivity.this, CommentActivity.class);
                        intent.putExtra(AppConstants.RECIPE_ID, recipe.getId());
                        startActivity(intent);
                    }
                });
                favoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new UpdateUserRecipeFav(recipe).execute();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Couldn't fetch results", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class UpdateUserRecipeFav extends AsyncTask<String, String, Boolean> {

        private RecipeModel recipe;

        public UpdateUserRecipeFav(RecipeModel recipeModel) {
            this.recipe = recipeModel;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean isSuccess = false;
            List<RecipeModel> recipes = recipesHelper.getUserRecipeFav(userId, accessToken);
            if (recipes != null) {
                if (recipe.isFav()) {
                    for (RecipeModel recipeModel : recipes) {
                        if (recipeModel.getId().equals(recipe.getId())) {
                            recipes.remove(recipeModel);
                            break;
                        }
                    }
                } else {
                    RecipeModel minRecipe = new RecipeModel();
                    minRecipe.setId(recipeID);
                    recipes.add(recipe);
                }
                isSuccess = recipesHelper.updateUserRecipeFav(recipes, userId, accessToken);
            } else {
                Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
            }
            return isSuccess;
        }

        @Override
        protected void onPostExecute(final Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            dialog.dismiss();
            recipe.setFav(recipe.isFav() ^ isSuccess);
            if (recipe.isFav())
                favoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_favorite_black_24dp));
            else
                favoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_black_24dp));
        }
    }

    public class DeleteRecipeTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(AppConstants.MJILIK_ENDPOINT+"/recipes/"+recipeID);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                httpURLConnection.setRequestProperty("Host", AppConstants.MJILIK_HOST_ENDPOINT);
                httpURLConnection.setRequestMethod("DELETE");
                httpURLConnection.connect();

                int HttpResult = httpURLConnection.getResponseCode();

                if (HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {
                    return "DELETED";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                Intent intent = new Intent(DetailActivity.this, HomeActivity.class);
                Toast.makeText(getApplicationContext(), "Recipe Deleted", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(), "Recipe could not be deleted", Toast.LENGTH_LONG).show();
            }
        }
    }
}
