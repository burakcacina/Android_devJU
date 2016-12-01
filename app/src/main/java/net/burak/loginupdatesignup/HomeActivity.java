package net.burak.loginupdatesignup;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.mijlk.service.Recipe;
import net.mijlk.service.RecipeRestService;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private RecipeListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Recipe> recipeList = new ArrayList<>();
    private RecipeRestService recipeRestService = new RecipeRestService();
    private EndlessRecyclerViewScrollListener scrollListener;
    private boolean loadingDataCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        super.showAdvertisement();
        Bundle bundle = getIntent().getExtras();

        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(this);
        String dataa = prefs2.getString("USERID", "no id"); //no id: default value
        System.out.println(dataa);

        if (bundle != null) {
            String json = bundle.getString("access_token");
            System.out.println(json);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recipeList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if(!loadingDataCompleted){
                    loadRecipesList(page);
                }
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);

        // specify an adapter (see also next example)
        mAdapter = new RecipeListAdapter(this, recipeList);
        mRecyclerView.setAdapter(mAdapter);

        loadRecipesList(1);

    }//TEST

    private void loadRecipesList(int pageNum){
        Call<List<Recipe>> call = recipeRestService.getRecipes(pageNum);
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                int statusCode = response.code();
                List<Recipe> recipesList = response.body();
                if(recipesList.size() == 0){
                    loadingDataCompleted = true;
                    scrollListener.setLastPage(true);
                }else{
                    mAdapter.updateRecipeList(recipesList);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // Log error here since request failed
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_update) {
            Intent intentUpdate = new Intent(getApplicationContext(), UpdateAccActivity.class);
            startActivity(intentUpdate);
        }
        else if(item.getItemId() == R.id.action_exit) {
            this.finishAffinity();
        }

        return true;
    }






}

