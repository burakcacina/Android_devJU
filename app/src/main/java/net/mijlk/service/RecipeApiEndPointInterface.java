package net.mijlk.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Farah on 26-11-2016.
 */

public interface RecipeApiEndPointInterface {

    @GET("recipes")
    Call<List<Recipe>> getRecipes(@Query("page") int pageNum);
}
