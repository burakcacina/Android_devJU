package net.burak.androidproject.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import net.burak.androidproject.AppConstants;
import net.burak.androidproject.models.DirectionsModel;
import net.burak.androidproject.models.RecipeModel;

import java.util.ArrayList;
import java.util.List;

public class DirectionsDB {
    private static final String TABLE_NAME = "DIRECTIONS";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (\n" +
            Fields.RECIPE_ID + " INTEGER PRIMARY KEY, \n" +
            Fields.ORDER + " INTEGER, \n" +
            Fields.DESCRIPTION + " TEXT \n" +
            ");";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static class Fields {
        private static final String RECIPE_ID = "RECIPE_ID";
        private static final String ORDER = "ORDER";
        private static final String DESCRIPTION = "DESCRIPTION";
    }

    private static List<ContentValues> convertFullRecipe(RecipeModel recipeModel) {
        List<ContentValues> rows = new ArrayList<>();
        for (DirectionsModel directionsModel : recipeModel.getDirections()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Fields.RECIPE_ID, recipeModel.getId());
            contentValues.put(Fields.ORDER, directionsModel.getOrder());
            contentValues.put(Fields.DESCRIPTION, directionsModel.getDescription());
            rows.add(contentValues);
        }
        return rows;
    }

    private static List<ContentValues> convertRecipeForUpdate(RecipeModel recipeModel) {
        List<ContentValues> rows = new ArrayList<>();
        for (DirectionsModel directionsModel : recipeModel.getDirections()) {
            ContentValues contentValues = new ContentValues();
            if (directionsModel.getDescription() != null)
                contentValues.put(Fields.DESCRIPTION, directionsModel.getDescription());
            if (directionsModel.getOrder() != null) {
                contentValues.put(Fields.ORDER, directionsModel.getOrder());
            }
        }
        return rows;
    }

    public static boolean upsertDirections(RecipeModel recipeModel, DBHelper dbHelper) {
        try {
            boolean isAlreadyExists = checkIfRecipeDirectionExistsExists(recipeModel, dbHelper);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (isAlreadyExists) {
                List<ContentValues> rows =  convertRecipeForUpdate(recipeModel);
                for (ContentValues row : rows) {
                    db.update(TABLE_NAME, row, Fields.RECIPE_ID + "=?", new String[]{"" + recipeModel.getId()});
                }
            } else {
                List<ContentValues> rows = convertFullRecipe(recipeModel);
                for (ContentValues row : rows) {
                    db.insert(TABLE_NAME, null, row);
                }
            }
            db.close();
            return true;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, e.getMessage());
        }
        return false;
    }

    private static boolean checkIfRecipeDirectionExistsExists(RecipeModel recipeModel, DBHelper dbHelper) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(TABLE_NAME, null, Fields.RECIPE_ID + "=? AND "+Fields.ORDER+"=?", new String[]{"" + recipeModel.getId(), ""+recipeModel}, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            db.close();
            return count > 0;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, e.getMessage());
        }
        return false;
    }
}
