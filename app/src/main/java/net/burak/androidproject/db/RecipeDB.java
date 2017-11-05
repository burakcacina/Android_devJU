package net.burak.androidproject.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import net.burak.androidproject.AppConstants;
import net.burak.androidproject.models.CreatorModel;
import net.burak.androidproject.models.RecipeModel;

import java.util.ArrayList;
import java.util.List;

public class RecipeDB {

    private static final String TABLE_NAME = "RECIPE";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (\n" +
            Fields.ID + " INTEGER PRIMARY KEY, \n" +
            Fields.NAME + " TEXT, \n" +
            Fields.DESCRIPTION + " TEXT, \n" +
            Fields.CREATOR_ID + " TEXT, \n" +
            Fields.CREATOR_NAME + " TEXT, \n" +
            Fields.IMAGE + " TEXT, \n" +
            Fields.CREATED + " INTEGER, \n" +
            Fields.FAV + " INTEGER, \n" +
            Fields.PAGE_NUMBER + " INTEGER \n" +
            ");";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static ContentValues convertFullRecipe(RecipeModel recipeModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Fields.ID, recipeModel.getId());
        contentValues.put(Fields.NAME, recipeModel.getName());
        if (recipeModel.getDescription() != null)
            contentValues.put(Fields.DESCRIPTION, recipeModel.getDescription());
        if (recipeModel.getCreator() != null) {
            contentValues.put(Fields.CREATOR_ID, recipeModel.getCreator().getId());
            contentValues.put(Fields.CREATOR_NAME, recipeModel.getCreator().getUserName());
        }
        contentValues.put(Fields.IMAGE, recipeModel.getImage());
        contentValues.put(Fields.CREATED, recipeModel.getCreated());
        contentValues.put(Fields.FAV, recipeModel.isFav() ? 1 : 0);
        contentValues.put(Fields.PAGE_NUMBER, recipeModel.getPageNumber());
        return contentValues;
    }

    private static ContentValues convertRecipeForUpdate(RecipeModel recipeModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Fields.NAME, recipeModel.getName());
        if (recipeModel.getDescription() != null)
            contentValues.put(Fields.DESCRIPTION, recipeModel.getDescription());
        if (recipeModel.getCreator() != null) {
            contentValues.put(Fields.CREATOR_ID, recipeModel.getCreator().getId());
            contentValues.put(Fields.CREATOR_NAME, recipeModel.getCreator().getUserName());
        }
        contentValues.put(Fields.IMAGE, recipeModel.getImage());
        contentValues.put(Fields.CREATED, recipeModel.getCreated());
        contentValues.put(Fields.FAV, recipeModel.isFav() ? 1 : 0);
        if (recipeModel.getPageNumber() != null)
            contentValues.put(Fields.PAGE_NUMBER, recipeModel.getPageNumber());
        return contentValues;
    }

    private static RecipeModel getRecipeFromCursor(Cursor c) {
        RecipeModel recipeModel = new RecipeModel();
        recipeModel.setId(c.getInt(c.getColumnIndex(Fields.ID)));
        recipeModel.setName(c.getString(c.getColumnIndex(Fields.NAME)));
        recipeModel.setDescription(c.getString(c.getColumnIndex(Fields.DESCRIPTION)));
        recipeModel.setImage(c.getString(c.getColumnIndex(Fields.IMAGE)));
        recipeModel.setCreated(c.getLong(c.getColumnIndex(Fields.CREATED)));
        recipeModel.setFav(c.getInt(c.getColumnIndex(Fields.FAV)) > 0);

        CreatorModel creator = new CreatorModel();
        creator.setId(c.getString(c.getColumnIndex(Fields.CREATOR_ID)));
        creator.setUserName(c.getString(c.getColumnIndex(Fields.CREATOR_NAME)));

        recipeModel.setCreator(creator);

        recipeModel.setPageNumber(c.getInt(c.getColumnIndex(Fields.PAGE_NUMBER)));

        return recipeModel;
    }

    public static boolean upsertRecipe(RecipeModel recipeModel, DBHelper dbHelper) {
        try {
            boolean isAlreadyExists = checkIfRecipeExists(recipeModel, dbHelper);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (isAlreadyExists) {
                db.update(TABLE_NAME, convertRecipeForUpdate(recipeModel), Fields.ID + "=?", new String[]{"" + recipeModel.getId()});
            } else {
                db.insert(TABLE_NAME, null, convertFullRecipe(recipeModel));
            }
            db.close();
            return true;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, e.getMessage());
        }
        return false;
    }

    private static boolean checkIfRecipeExists(RecipeModel recipeModel, DBHelper dbHelper) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(TABLE_NAME, null, Fields.ID + "=?", new String[]{"" + recipeModel.getId()}, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            db.close();
            return count > 0;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, e.getMessage());
        }
        return false;
    }

    public static RecipeModel getRecipeById(Integer recipeId, DBHelper dbHelper) {
        RecipeModel recipeModel = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + Fields.ID + "=?", new String[]{"" + recipeId});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                recipeModel = getRecipeFromCursor(cursor);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, e.getMessage());
        }
        return recipeModel;
    }

    public static List<RecipeModel> getRecipes(Integer pageNumber, DBHelper dbHelper) {
        List<RecipeModel> recipes = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + Fields.PAGE_NUMBER + "=?", new String[]{"" + pageNumber});
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    recipes = new ArrayList<>();
                    do {
                        recipes.add(0, getRecipeFromCursor(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, e.getMessage());
        }
        return recipes;
    }

    private static class Fields {
        private static final String ID = "ID";
        private static final String NAME = "NAME";
        private static final String DESCRIPTION = "DESCRIPTION";
        private static final String CREATED = "CREATED";
        private static final String CREATOR_NAME = "CREATOR_NAME";
        private static final String CREATOR_ID = "CREATOR_ID";
        private static final String IMAGE = "IMAGE";
        private static final String FAV = "FAV";
        private static final String PAGE_NUMBER = "PAGE_NUMBER";
    }
}
