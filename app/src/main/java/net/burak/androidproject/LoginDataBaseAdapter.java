package net.burak.androidproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/* This is Created
        by
      BURAK CACINA
*/

public class LoginDataBaseAdapter
{
	static final String DATABASE_NAME = "loginuser.db";
	static final int DATABASE_VERSION = 1;

	static final String DATABASE_CREATE = "create table "+"LOGINUSER"+
			"( " +"ID"+" integer primary key autoincrement,"+ "USERNAME  text,USERID text); ";

	public  SQLiteDatabase db;
	private final Context context;
	private DataBaseHelper dbHelper;
	public LoginDataBaseAdapter(Context _context)
	{
		context = _context;
		dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	public LoginDataBaseAdapter open() throws SQLException
	{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	public void close()
	{
		db.close();
	}

	public  SQLiteDatabase getDatabaseInstance()
	{
		return db;
	}

	public void insertEntry(String userName,String userID)
	{
		ContentValues newValues = new ContentValues();

		newValues.put("USERNAME", userName);
		newValues.put("USERID",userID);

		db.insert("LOGINUSER", null, newValues);
	}
	public String getSinlgeEntry(String userName)
	{
		Cursor cursor=db.query("LOGINUSER", null, " USERNAME=?", new String[]{userName}, null, null, null);
		if(cursor.getCount()<1) // UserName Not Exist
		{
			cursor.close();
			return "NOT EXIST";
		}
		cursor.moveToFirst();
		String userID= cursor.getString(cursor.getColumnIndex("USERID"));
		cursor.close();
		return userID;
	}
	public void  updateEntry(String userName,String userID)
	{
		ContentValues updatedValues = new ContentValues();
		updatedValues.put("USERNAME", userName);
		updatedValues.put("USERID",userID);

		String where="USERNAME = ?";
		db.update("LOGINUSER",updatedValues, where, new String[]{userName});
	}
	public Integer Deleteuser(String userID)
	{
		return db.delete("LOGINUSER","USERID = ?",new String[] {userID});
	}


}