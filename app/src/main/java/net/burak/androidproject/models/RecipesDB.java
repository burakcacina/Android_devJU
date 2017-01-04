package net.burak.androidproject.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/* This is Created
        by
      BURAK CACINA
*/

public class RecipesDB {

    public static SQLiteDatabase createDB(Context context)
    {
        SQLiteDatabase db = context.openOrCreateDatabase("main", Context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS Responses(resp VARCHAR, page INTEGER);");

        return db;
    }

    public static void insertResponse(String resp, int page, Context context)
    {
        Log.v("DB", "REPLACE INTO Responses VALUES('" + resp + "', '"+ page + "');");

        SQLiteDatabase db = RecipesDB.createDB(context);
        db.execSQL("REPLACE INTO Responses VALUES('" + resp + "', '"+ page + "');");

        db.close();
    }

    public static String fetchResp(int page, Context context)
    {
        SQLiteDatabase db = RecipesDB.createDB(context);

        Cursor resultSet = db.rawQuery("SELECT resp FROM Responses WHERE page="+page, null);

        try {
            resultSet.moveToFirst();
            String res = resultSet.getString(0);
            db.close();

            Log.v("DB", "response returned");

            return res;
        }
        catch (Exception e)
        {
            return "[]";
        }
    }

    public static boolean isInternetconnected(Context ct) {
        boolean connected = false;
        //get the connectivity manager object to identify the network state.
        ConnectivityManager connectivityManager = (ConnectivityManager)ct.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Check if the manager object is NULL, this check is required. to prevent crashes in few devices.
        if(connectivityManager != null) {
            //Check Mobile data or Wifi net is present

            //we are connected to a network
            connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
            return connected;
        } else  {
            return false;
        }
    }
}
