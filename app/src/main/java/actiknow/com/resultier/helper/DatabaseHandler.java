package actiknow.com.resultier.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import actiknow.com.resultier.model.User;
import actiknow.com.resultier.util.AppConfigTags;
import actiknow.com.resultier.util.Utils;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 12;

    // Database Name
    private static final String DATABASE_NAME = "resultier";

    // Table Names
    private static final String TABLE_USER = "user";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // QUESTIONS Table - column names
    private static final String KEY_QUESTION = "question";

    // ATMS Table - column names
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";



    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USER
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT,"  + KEY_MOBILE + " TEXT" + ")";


    public DatabaseHandler(Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL (CREATE_TABLE_USERS);

    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate (db);
    }


    // ------------------------ "atms" table methods ----------------//

    public long createUser (User user) {
        SQLiteDatabase db = this.getWritableDatabase ();
        ContentValues values = new ContentValues();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating User", false);
        values.put (KEY_ID, user.getUser_id ());
        values.put (KEY_NAME, user.getUser_name());
        values.put (KEY_EMAIL, user.getUser_email());
        values.put (KEY_MOBILE, user.getUser_mobile ());
        long user_id = db.insert (TABLE_USER, null, values);
        return user_id;
    }

    public List<User> getAllUsers () {
        List<User> users = new ArrayList<User>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER +   " WHERE " + KEY_ID + " = " + 30;
        Log.e("SELECTQUERY", selectQuery);
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all user", false);
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor c = db.rawQuery (selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst ()) {
            do {
                User user = new User ();
                user.setUser_id (c.getInt (c.getColumnIndex (KEY_ID)));
                user.setUser_name (c.getString (c.getColumnIndex (KEY_NAME)));
                user.setUser_email (c.getString (c.getColumnIndex (KEY_EMAIL)));
                user.setUser_mobile (c.getString (c.getColumnIndex (KEY_MOBILE)));
                users.add (user);
            } while (c.moveToNext ());
        }
        return users;
    }

    public int getUserCount () {
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get total atm count : " + count, false);
        return count;
    }

   /* public int updateUser (User user) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update atm", false);
        ContentValues values = new ContentValues();
        values.put (KEY_ATM_ID, atm.getAtm_id ());
        values.put (KEY_ATM_UNIQUE_ID, atm.getAtm_unique_id ());
        values.put (KEY_AGENCY_ID, atm.getAtm_agency_id ());
        values.put (KEY_LAST_AUDIT_DATE, atm.getAtm_last_audit_date ());
        values.put (KEY_BANK_NAME, atm.getAtm_bank_name ());
        values.put (KEY_ADDRESS, atm.getAtm_address ());
        values.put (KEY_CITY, atm.getAtm_city ());
        values.put (KEY_PINCODE, atm.getAtm_pincode ());
        // updating row
        return db.update (TABLE_ATMS, values, KEY_ID + " = ?", new String[] {String.valueOf (atm.getAtm_id ())});
    }*/

  /*  public void deleteAtm (long atm_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete atm where ID = " + atm_id, false);
        db.delete (TABLE_ATMS, KEY_ID + " = ?", new String[] {String.valueOf (atm_id)});
    }*/

    public void deleteAllUsers () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all atm", false);
        db.execSQL ("delete from " + TABLE_USER);
    }



    public void closeDB () {
        SQLiteDatabase db = this.getReadableDatabase ();
        if (db != null && db.isOpen ())
            db.close ();
    }

    private String getDateTime () {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault ());
        Date date = new Date();
        return dateFormat.format (date);
    }
}