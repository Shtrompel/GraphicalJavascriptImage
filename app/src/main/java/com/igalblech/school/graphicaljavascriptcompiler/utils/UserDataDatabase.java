package com.igalblech.school.graphicaljavascriptcompiler.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import lombok.Setter;


// For basic single table databases

public class UserDataDatabase extends SQLiteOpenHelper {

    public static class Constants {
        public static final int DATABASE_VERSION = 1;

        public static final String DATABASE_NAME = "userdata.db";
        public static final String TABLE_NAME = "userdata";

        public static final String COLUMN_KEY = "id";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_IS_VERIFIED = "is_verified";
        public static final String COLUMN_VERIFICATION_CODE = "verification_code";
    }

    public class RetClass {
        public UserData userData;
        public @Setter int value;
    }

    public interface UserdataIterable {
        RetClass onIteration(SQLiteDatabase sqLiteDatabase, UserData chosen, UserData in, String str1, String str2);
    }

    public UserDataDatabase ( @Nullable Context context ) {
        this ( context, null );
    }

    public UserDataDatabase ( @Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory ) {
        super ( context, Constants.DATABASE_NAME, factory, Constants.DATABASE_VERSION );
    }

    private static String formatColumn(String name, String type, boolean addComma) {
        return String.format (
                "%s %s %s ",
                name,
                type,
                addComma ? "," : ""
        );
    }

    private static String formatColumn(String name, String type){
        return formatColumn(name, type, true);
    }

    @Override
    public void onCreate ( SQLiteDatabase sqLiteDatabase ) {
        StringBuilder stringBuilder = new StringBuilder ( );
        stringBuilder.append ( String.format ( "CREATE TABLE %s (", Constants.TABLE_NAME ) );
        stringBuilder.append (formatColumn(Constants.COLUMN_USERNAME, "TEXT"));
        stringBuilder.append (formatColumn(Constants.COLUMN_PASSWORD, "TEXT"));
        stringBuilder.append (formatColumn(Constants.COLUMN_EMAIL, "TEXT"));
        stringBuilder.append (formatColumn(Constants.COLUMN_PHONE, "TEXT"));
        stringBuilder.append (formatColumn(Constants.COLUMN_IS_VERIFIED, "INTEGER"));
        stringBuilder.append (formatColumn(Constants.COLUMN_VERIFICATION_CODE, "TEXT"));
        stringBuilder.append (formatColumn(Constants.COLUMN_KEY, "TEXT", false));
        stringBuilder.append ( ");" );

        sqLiteDatabase.execSQL(stringBuilder.toString ());
    }

    @Override
    public void onUpgrade ( SQLiteDatabase sqLiteDatabase, int i, int i1 ) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public int registerUser(UserData userData, String validationPassword) {

        int err;

        if ((err = UserDataValidator.validateUsername(userData.getUsername())) != UserDataValidator.ERROR_NONE) {
            return err;
        }

        if ((err = UserDataValidator.validateEmail(userData.getEmail())) != UserDataValidator.ERROR_NONE) {
            return err;
        }

        if ((err = UserDataValidator.validatePhone(userData.getPhone())) != UserDataValidator.ERROR_NONE) {
            return err;
        }

        if ((err = UserDataValidator.validatePassword(userData.getPassword())) != UserDataValidator.ERROR_NONE) {
            return err;
        }

        if (!validationPassword.equals(userData.getPassword())) {
            err = UserDataValidator.ERROR_DIF_PASSWORDS;
            return err;
        }

        if (userData.getUsername().equals(userData.getPassword())) {
            err = UserDataValidator.ERROR_USERNAME_DIF_PASSWORD;
            return err;

        }

        if ((err = checkForClashes(userData)) != UserDataValidator.ERROR_NONE) {
            return err;
        }

        addUserdata(userData);

        return err;
    }

    public void addUserdata( UserData userData ) {
        ContentValues values = userDataToContentValues(userData);
        /*
        values.put(Constants.COLUMN_USERNAME, userData.getUsername ());
        values.put(Constants.COLUMN_PASSWORD, userData.getPassword ());
        values.put(Constants.COLUMN_EMAIL, userData.getEmail ());
        values.put(Constants.COLUMN_PHONE, userData.getPhone ());
        values.put(Constants.COLUMN_IS_VERIFIED, userData.isVerified () ? 1 : 0);
        values.put(Constants.COLUMN_VERIFICATION_CODE, userData.getVerificationCode ());
        */
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(Constants.TABLE_NAME, null, values);
        db.close();
    }

    public UserData cursorToUserdata(Cursor cursor) {
        UserData userData = new UserData (  );
        userData.setUsername ( cursor.getString ( cursor.getColumnIndex( Constants.COLUMN_USERNAME ) ) );
        userData.setPassword ( cursor.getString ( cursor.getColumnIndex( Constants.COLUMN_PASSWORD ) ) );
        userData.setEmail ( cursor.getString ( cursor.getColumnIndex( Constants.COLUMN_EMAIL ) ) );
        userData.setPhone ( cursor.getString ( cursor.getColumnIndex( Constants.COLUMN_PHONE ) ) );
        userData.setVerificationCode ( cursor.getString ( cursor.getColumnIndex( Constants.COLUMN_VERIFICATION_CODE ) ) );
        userData.setVerified ( cursor.getInt ( cursor.getColumnIndex( Constants.COLUMN_IS_VERIFIED ) ) == 1 ? true : false );
        return userData;
    }

    public ContentValues userDataToContentValues(UserData userData) {
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_USERNAME, userData.getUsername ());
        values.put(Constants.COLUMN_PASSWORD, userData.getPassword ());
        values.put(Constants.COLUMN_EMAIL, userData.getEmail ());
        values.put(Constants.COLUMN_PHONE, userData.getPhone ());
        values.put(Constants.COLUMN_IS_VERIFIED, userData.isVerified () ? 1 : 0);
        values.put(Constants.COLUMN_VERIFICATION_CODE, userData.getVerificationCode ());
        return values;
    }

/*
    public int getRowCount() {
        SQLiteDatabase database = getReadableDatabase ();
        long size = DatabaseUtils.queryNumEntries(database, Constants.TABLE_NAME);
        database.close ();
        return (int)size;
    }

    public int getUserdataByIndex() {

    }
*/

    public RetClass iterateThroughRows ( UserdataIterable userdataIterable, UserData in) {
        return iterateThroughRows (userdataIterable, in ,null, null);
    }

    public RetClass iterateThroughRows ( UserdataIterable userdataIterable,
                                         UserData in,
                                         String str1,
                                         String str2) {


        SQLiteDatabase sqLiteDatabase = getWritableDatabase ();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + Constants.TABLE_NAME, null);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            DatabaseUtils.dumpCurrentRowToString(cursor);
/*
            UserData userData = new UserData (  );
            userData.setUsername ( cursor.getString ( cursor.getColumnIndex( Constants.COLUMN_USERNAME ) ) );
            userData.setPassword ( cursor.getString ( cursor.getColumnIndex( Constants.COLUMN_PASSWORD ) ) );
            userData.setEmail ( cursor.getString ( cursor.getColumnIndex( Constants.COLUMN_EMAIL ) ) );
            userData.setPhone ( cursor.getString ( cursor.getColumnIndex( Constants.COLUMN_PHONE ) ) );
            userData.setVerificationCode ( cursor.getString ( cursor.getColumnIndex( Constants.COLUMN_VERIFICATION_CODE ) ) );
            userData.setVerified ( cursor.getInt ( cursor.getColumnIndex( Constants.COLUMN_IS_VERIFIED ) ) == 1 ? true : false );
*/

            UserData userData = cursorToUserdata(cursor);

            RetClass x = userdataIterable.onIteration ( sqLiteDatabase, userData, in, str1, str2 );
            if (x != null)
                return x;
        }
        cursor.close ();
        sqLiteDatabase.close ();

        return null;
    }

    public int checkForClashes(UserData userData) {
        RetClass retClass = iterateThroughRows ( ( sqLiteDatabase, chosen, in, str1, str2 ) -> {
            Log.d("developer", chosen.toString ());
            Log.d("developer", in.toString ());
            int v = userData.checkForClashes ( chosen );
            if (v != UserDataValidator.ERROR_NONE) {
                RetClass rc = new RetClass ();
                rc.setValue ( v );
                return rc;
            }
            else
                return null;
            },
                userData );

        if (retClass != null)
            return retClass.value;
        else
            return UserDataValidator.ERROR_NONE;
    }

    public UserData loginUser(String username, String password) {

        String sqlScript = String.format ( "SELECT * FROM %s WHERE (%s = '%s' OR %s = '%s' OR %s = '%s') AND %s = '%s';",
                Constants.TABLE_NAME,
                Constants.COLUMN_USERNAME,
                username,
                Constants.COLUMN_EMAIL,
                username.toLowerCase (),
                Constants.COLUMN_PHONE,
                username.replace ( "-", "" ).replace ( " ", "" ),
                Constants.COLUMN_PASSWORD,
                password
                );

        SQLiteDatabase sqLiteDatabase = getWritableDatabase ();
        Cursor cursor =  sqLiteDatabase.rawQuery( sqlScript, null);

        if (cursor != null) {
            if (cursor.moveToFirst ()) {
                return cursorToUserdata(cursor);
            }
        }

        return null;
    }

    public UserData findUserViaEmailOrPhone(String email, String phone) {

        String sqlScript = String.format ( "SELECT * FROM %s WHERE %s = '%s' OR %s = '%s';",
                Constants.TABLE_NAME,
                Constants.COLUMN_EMAIL,
                email.toLowerCase (),
                Constants.COLUMN_PHONE,
                phone.replace ( "-", "" ).replace ( " ", "" ));

        SQLiteDatabase sqLiteDatabase = getWritableDatabase ();
        Cursor cursor =  sqLiteDatabase.rawQuery( sqlScript, null);

        if (cursor != null) {
            if (cursor.moveToFirst ()) {
                return cursorToUserdata(cursor);
            }
        }

        return null;
    }

    public UserData findUserViaCode(String code) {

        String sqlScript = String.format ( "SELECT * FROM %s WHERE %s = '%s';",
                Constants.TABLE_NAME,
                Constants.COLUMN_VERIFICATION_CODE,
                code );

        SQLiteDatabase sqLiteDatabase = getWritableDatabase ();
        Cursor cursor =  sqLiteDatabase.rawQuery( sqlScript, null);

        if (cursor != null) {
            if (cursor.moveToFirst ()) {
                return cursorToUserdata(cursor);
            }
        }

        return null;
    }

    public void changePassword(UserData userData, String password) {

        // https://stackoverflow.com/questions/11563732/change-a-value-in-a-column-in-sqlite
        String sqlScript = String.format ( "UPDATE %s SET %s = '%s' WHERE %s = '%s';",
                Constants.TABLE_NAME,
                Constants.COLUMN_PASSWORD,
                password,
                Constants.COLUMN_USERNAME,
                userData.getUsername ());

        SQLiteDatabase sqLiteDatabase = getWritableDatabase ();
        sqLiteDatabase.execSQL ( sqlScript );
        sqLiteDatabase.close ();
    }

    public void changeCode(UserData userData, String code) {

        String sqlScript = String.format ( "UPDATE %s SET %s = '%s' WHERE %s = '%s';",
                Constants.TABLE_NAME,
                Constants.COLUMN_VERIFICATION_CODE,
                code,
                Constants.COLUMN_USERNAME,
                userData.getUsername ());

        SQLiteDatabase sqLiteDatabase = getWritableDatabase ();
        sqLiteDatabase.execSQL ( sqlScript );
        sqLiteDatabase.close ();
    }

    public void validateUser(UserData userData) {

/*
        // https://stackoverflow.com/questions/11563732/change-a-value-in-a-column-in-sqlite
        String sqlScript = String.format ( "UPDATE %s SET %s = %d WHERE %s = '%s';",
                Constants.TABLE_NAME,
                Constants.COLUMN_KEY,
                1,
                Constants.COLUMN_USERNAME,
                userData.getUsername ());

        SQLiteDatabase sqLiteDatabase = getWritableDatabase ();
        sqLiteDatabase.execSQL ( sqlScript );
        sqLiteDatabase.close ();
        */


        userData.setVerified ( true );

        ContentValues cv = new ContentValues ( );
        cv.put ( Constants.COLUMN_IS_VERIFIED, 1 );

        SQLiteDatabase sqLiteDatabase = getWritableDatabase ();
        //sqLiteDatabase.execSQL ( sqlScript );

        int v = sqLiteDatabase.update (
                Constants.TABLE_NAME,
                cv ,
                Constants.COLUMN_USERNAME + " = ?",
                new String[]{userData.getUsername ()} );
        Log.d("developer", "update: " + v);

        sqLiteDatabase.close ();

    }



    // addUser - in user data, validation password
    // checkForClashes - in Userdata
    // loginUser - in password,username out user data
    // changePassword - in UserData, password
    // findUserViaCode
}
