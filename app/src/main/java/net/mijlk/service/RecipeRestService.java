/*
package net.mijlk.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

*
 * Created by Farah on 25-11-2016.


public class RecipeRestService {

    private static final String BASE_URL = "http://52.211.99.140/api/v1/";

    private Retrofit retrofit;

    private Gson gson;

    private RecipeApiEndPointInterface recipeApi;

    public RecipeRestService(){
        gson = new GsonBuilder()
//                .setDateFormat("dd.MM.yyyy")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        recipeApi = retrofit.create(RecipeApiEndPointInterface.class);
    }

    public Call<List<Recipe>> getRecipes(int pageNum){
        return recipeApi.getRecipes(pageNum);
    }


}
*/