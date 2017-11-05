package net.burak.androidproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import net.burak.androidproject.helpers.RecipesHelper;
import net.burak.androidproject.models.RecipeModel;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* This is Created
        by
      BURAK CACINA
*/

public class HomeActivity extends AppCompatActivity {

    private AbsListView lvRecipes;
    private ProgressDialog dialog;
    private AtomicInteger pageNum = new AtomicInteger(1);
    private RecipesHelper recipesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        this.recipesHelper = new RecipesHelper(this);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start

        lvRecipes = (AbsListView) findViewById(R.id.lvRecipes);

        Button nextPageBtn = (Button) findViewById(R.id.pagenext);
        Button prevPageBtn = (Button) findViewById(R.id.pageprev);
        Button but3 = (Button) findViewById(R.id.createRecipe);

        new GetRecipeByPageTask().execute("" + pageNum.get());

        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetRecipeByPageTask().execute("" + pageNum.incrementAndGet());
            }
        });

        prevPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageNum.get() > 1) {
                    new GetRecipeByPageTask().execute("" + pageNum.decrementAndGet());
                } else {
                    Toast.makeText(getApplicationContext(), "This is the first page", Toast.LENGTH_SHORT).show();
                }
            }
        });


        but3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                {
                    Intent intent = new Intent(HomeActivity.this, CreateRecipeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            Intent intentUpdate = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intentUpdate);
        } else if (item.getItemId() == R.id.action_showuser) {
            Intent intentUpdate = new Intent(getApplicationContext(), GetuserInformationActivity.class);
            startActivity(intentUpdate);
        } else if (item.getItemId() == R.id.action_update) {
            Intent intentUpdate = new Intent(getApplicationContext(), UpdateAccActivity.class);
            startActivity(intentUpdate);
        } else if (item.getItemId() == R.id.action_exit) {
            this.finishAffinity();
        } else if (item.getItemId() == R.id.action_created) {
            Intent intentUpdatea = new Intent(getApplicationContext(), UsersRecipeAndComments.class);
            startActivity(intentUpdatea);
        }

        return true;
    }

    public class GetRecipeByPageTask extends AsyncTask<String, String, List<RecipeModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<RecipeModel> doInBackground(String... params) {
            return recipesHelper.getRecipes(Integer.parseInt(params[0]));
        }

        @Override
        protected void onPostExecute(final List<RecipeModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result != null) {
                RecipeAdapter adapter = new RecipeAdapter(getApplicationContext(), R.layout.activity_showrecipe, result);
                lvRecipes.setAdapter(adapter);
                lvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        RecipeModel recipeModel = result.get(position);
                        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                        intent.putExtra(AppConstants.RECIPE_ID, "" + recipeModel.getId());
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, no internet connection found.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class RecipeAdapter extends ArrayAdapter {

        private List<RecipeModel> recipes;
        private int resource;
        private LayoutInflater inflater;

        public RecipeAdapter(Context context, int resource, List<RecipeModel> objects) {
            super(context, resource, objects);
            recipes = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RecipeModel currRecipe = recipes.get(position);

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivRecipeIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
                holder.tvRecipeName = (TextView) convertView.findViewById(R.id.tvRecipeName);
                holder.tvRecipeID = (TextView) convertView.findViewById(R.id.tvRecipeID);
                holder.tvCreated = (TextView) convertView.findViewById(R.id.tvCreated);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            ImageLoader.getInstance().displayImage(recipes.get(position).getImage(), holder.ivRecipeIcon, new ImageLoadingListener() {
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

            holder.tvRecipeName.setText(currRecipe.getName());
            holder.tvRecipeID.setText("ID: " + currRecipe.getId());
            holder.tvCreated.setText("Created: " + currRecipe.getCreated());
            return convertView;
        }


        class ViewHolder {
            private ImageView ivRecipeIcon;
            private TextView tvRecipeName;
            private TextView tvRecipeID;
            private TextView tvCreated;

        }
    }


}

