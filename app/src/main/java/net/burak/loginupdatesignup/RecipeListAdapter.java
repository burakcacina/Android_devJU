package net.burak.loginupdatesignup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mijlk.service.Recipe;

import java.util.List;


public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {
    private Context mContext;
    private List<Recipe> recipeList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public ImageView thumbnail;
        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.info_text);
            thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecipeListAdapter(Context mContext, List<Recipe> recipeList) {
        this.mContext = mContext;
        this.recipeList = recipeList;
    }

    public void updateRecipeList(List<Recipe> recipeList){
        this.recipeList.addAll(recipeList);
        this.notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.recipe_card_item, parent, false);
        // set the view's size, margins, paddings and layout parameter
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(this.recipeList.get(position).getName());
        String imageUrl = this.recipeList.get(position).getImage();
        if(imageUrl == null || imageUrl.isEmpty()){
            Glide.with(mContext).load(R.mipmap.ic_launcher).into(holder.thumbnail);
        }else{
            // loading album cover using Glide library
            Glide.with(mContext).load(imageUrl).override(75, 75).into(holder.thumbnail);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.recipeList.size();
    }
}