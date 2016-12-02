package net.burak.loginupdatesignup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LoginDataBaseAdapter
{
	static final String DATABASE_NAME = "login3d.db";
	static final int DATABASE_VERSION = 1;
	public static final int NAME_COLUMN = 1;
	// TODO: Create public field for each column in your table.
	// SQL Statement to create a new database.
	static final String DATABASE_CREATE = "create table "+"LOGIN3D"+
			"( " +"ID"+" integer primary key autoincrement,"+ "USERNAME  text,USERID text); ";
	// Variable to hold the database instance
	public  SQLiteDatabase db;
	// Context of the application using the database.
	private final Context context;
	// Database open/upgrade helper
	private DataBaseHelper dbHelper;
	public LoginDataBaseAdapter(Context _context)
	{
		context = _context;
		dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	public  LoginDataBaseAdapter open() throws SQLException
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
		// Assign values for each row.
		newValues.put("USERNAME", userName);
		newValues.put("USERID",userID);

		// Insert the row into your table
		db.insert("LOGIN3D", null, newValues);
		///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
	}
	public String getSinlgeEntry(String userName)
	{
		Cursor cursor=db.query("LOGIN3D", null, " USERNAME=?", new String[]{userName}, null, null, null);
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
		// Define the updated row content.
		ContentValues updatedValues = new ContentValues();
		// Assign values for each row.
		updatedValues.put("USERNAME", userName);
		updatedValues.put("USERID",userID);

		String where="USERNAME = ?";
		db.update("LOGIN3D",updatedValues, where, new String[]{userName});
	}
}