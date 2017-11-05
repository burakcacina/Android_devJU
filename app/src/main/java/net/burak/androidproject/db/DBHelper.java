package net.burak.androidproject.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import net.burak.androidproject.AppConstants;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, AppConstants.DB_NAME, null, AppConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecipeDB.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int _oldVersion, int _newVersion) {
        Log.w(AppConstants.LOG_TAG, "Upgrading from version " + _oldVersion + " to " + _newVersion + ", which will destroy all old data");
        db.execSQL(RecipeDB.DROP_TABLE);
        onCreate(db);
    }
}
