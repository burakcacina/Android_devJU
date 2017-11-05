package net.burak.androidproject.helpers;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.burak.androidproject.AppConstants;
import net.burak.androidproject.db.DBHelper;
import net.burak.androidproject.db.RecipeDB;
import net.burak.androidproject.models.RecipeModel;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RecipesHelper {

    private static final Gson gson = new Gson();
    private static final String GET_RECIPES_BY_PAGE = AppConstants.MJILIK_ENDPOINT + "/recipes?page=";
    private static final String GET_RECIPE_BY_ID = AppConstants.MJILIK_ENDPOINT + "/recipes/";
    private static final String ADD_RECIPE_FAV = AppConstants.MJILIK_ENDPOINT + "/accounts/%s/favorites";
    private static final String SEARCH_RECIPE = AppConstants.MJILIK_ENDPOINT+"/recipes/search?term=";
    private static final Type recipeListType = new TypeToken<List<RecipeModel>>() {
    }.getType();
    private DBHelper dbHelper;


    public RecipesHelper(Context context) {
        dbHelper = new DBHelper(context);
    }

    public List<RecipeModel> getRecipes(Integer pageNumber) {
        List<RecipeModel> recipes = getRecipesFromServer(pageNumber);
        if (recipes == null)
            recipes = RecipeDB.getRecipes(pageNumber, dbHelper);
        return recipes;
    }

    private List<RecipeModel> getRecipesFromServer(Integer pageNumber) {
        List<RecipeModel> recipes = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(GET_RECIPES_BY_PAGE + pageNumber);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Host", AppConstants.MJILIK_HOST_ENDPOINT);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            String response = IOUtils.toString(httpURLConnection.getInputStream(), StandardCharsets.UTF_8);
            recipes = gson.fromJson(response, recipeListType);
            for (RecipeModel recipe : recipes) {
                recipe.setPageNumber(pageNumber);
                RecipeDB.upsertRecipe(recipe, dbHelper);
            }
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, e.getMessage());
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return recipes;
    }

    public List<RecipeModel> searchRecipesInServer(String q) {
        List<RecipeModel> recipes = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(SEARCH_RECIPE + q);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Host", AppConstants.MJILIK_HOST_ENDPOINT);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            String response = IOUtils.toString(httpURLConnection.getInputStream(), StandardCharsets.UTF_8);
            recipes = gson.fromJson(response, recipeListType);
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, e.getMessage());
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return recipes;
    }

    public RecipeModel getRecipeById(Integer recipeId) {
        RecipeModel recipe = getRecipeFromServer(recipeId);
        if (recipe == null)
            recipe = RecipeDB.getRecipeById(recipeId, dbHelper);
        return recipe;
    }

    private RecipeModel getRecipeFromServer(Integer recipeId) {
        RecipeModel recipe = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(GET_RECIPE_BY_ID + recipeId);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Host", AppConstants.MJILIK_HOST_ENDPOINT);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            String response = IOUtils.toString(httpURLConnection.getInputStream(), StandardCharsets.UTF_8);
            recipe = gson.fromJson(response, RecipeModel.class);
            RecipeDB.upsertRecipe(recipe, dbHelper);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return recipe;
    }

    public boolean updateUserRecipeFav(List<RecipeModel> recipeModels, String userId, String accessToken) {
        return updateUserRecipeFavInServer(recipeModels, userId, accessToken);
    }

    public List<RecipeModel> getUserRecipeFav(String userId, String accessToken) {
        List<RecipeModel> recipes = getUserRecipeFavInServer(userId, accessToken);
        return recipes;
    }

    private List<RecipeModel> getUserRecipeFavInServer(String userId, String accessToken) {
        List<RecipeModel> recipes = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(String.format(ADD_RECIPE_FAV, userId));
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            httpURLConnection.setRequestProperty("Host", AppConstants.MJILIK_HOST_ENDPOINT);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            String response = IOUtils.toString(httpURLConnection.getInputStream(), StandardCharsets.UTF_8);
            recipes = gson.fromJson(response, recipeListType);
            for (RecipeModel recipe : recipes) {
                recipe.setFav(true);
                RecipeDB.upsertRecipe(recipe, dbHelper);
            }

        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, e.getMessage());
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return recipes;
    }

    private boolean updateUserRecipeFavInServer(List<RecipeModel> recipeModels, String userId, String accessToken) {
        try {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(String.format(ADD_RECIPE_FAV, userId));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                httpURLConnection.setRequestProperty("Host", AppConstants.MJILIK_HOST_ENDPOINT);
                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.connect();

                final List<RecipeModel> refactoredRecipeModels = new ArrayList<>();
                for (RecipeModel recipeModel : recipeModels) {
                    RecipeModel minRecipe = new RecipeModel();
                    minRecipe.setId(recipeModel.getId());
                    refactoredRecipeModels.add(minRecipe);
                }
                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                out.write(gson.toJson(refactoredRecipeModels));
                out.close();

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                    return true;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, e.getMessage());
        }
        return false;
    }
}
