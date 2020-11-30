package com.igalblech.school.graphicaljavascriptcompiler.utils.gallery;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;

import com.igalblech.school.graphicaljavascriptcompiler.utils.project.ProjectSettings;
import com.igalblech.school.graphicaljavascriptcompiler.utils.userdata.UserDataDatabase;

import org.apache.harmony.awt.ContextStorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectSettingsDatabase extends SQLiteOpenHelper {

    public static class Constants {
        public static final int DATABASE_VERSION = 1;

        public static final String DATABASE_NAME = "projects.db";
        public static final String PRIVATE_TABLE_NAME = "private_projects";
        public static final String PUBLIC_TABLE_NAME = "public_projects";

        public static final String COLUMN_DATA = "data";
        public static final String COLUMN_CREATED_DATE = "created_date";
        public static final String COLUMN_ID = "private_id";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_PUBLIC_VIEWS = "views";
        public static final String COLUMN_PUBLIC_RATING = "rating";
    }

    private static String formatColumn(String name, String type) {
        return String.format (
                "%s %s",
                name,
                type
        );
    }

    public ProjectSettingsDatabase ( @Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory ) {
        super ( context, Constants.DATABASE_NAME, factory, Constants.DATABASE_VERSION );
    }

    @Override
    public void onCreate ( SQLiteDatabase db ) {

        String stringBuilderPrivate = String.format ( Locale.ENGLISH,
                "CREATE TABLE %s (%s, %s, %s, %s, %s)",
                Constants.PRIVATE_TABLE_NAME,
                formatColumn ( Constants.COLUMN_ID, "INTEGER" ),
                formatColumn ( Constants.COLUMN_DATA, "BLOB" ),
                formatColumn ( Constants.COLUMN_CREATED_DATE, "TEXT"),
                formatColumn ( Constants.COLUMN_USERNAME, "TEXT"),
                formatColumn ( Constants.COLUMN_TITLE, "TEXT")
                );
        db.execSQL( stringBuilderPrivate );

        String stringBuilderPublic = String.format ( Locale.ENGLISH,
                "CREATE TABLE %s (%s, %s, %s, %s, %s, %s, %s)",
                Constants.PUBLIC_TABLE_NAME,
                formatColumn ( Constants.COLUMN_ID, "INTEGER" ),
                formatColumn ( Constants.COLUMN_DATA, "BLOB" ),
                formatColumn ( Constants.COLUMN_CREATED_DATE, "TEXT"),
                formatColumn ( Constants.COLUMN_USERNAME, "TEXT"),
                formatColumn ( Constants.COLUMN_TITLE, "TEXT"),
                formatColumn ( Constants.COLUMN_PUBLIC_VIEWS, "INTEGER"),
                formatColumn ( Constants.COLUMN_PUBLIC_RATING, "FLOAT")
        );
        db.execSQL( stringBuilderPublic );
    }

    @Override
    public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<ProjectSettings> toList(int start, int end, String tableName, String sort) {
        List<ProjectSettings> ret = new ArrayList<> (  );

        SQLiteDatabase db = getReadableDatabase ();
        String dbQuery = String.format ( Locale.ENGLISH,
                "SELECT * FROM %s ORDER BY %s DESC LIMIT %d",
                tableName,
                sort,
                end
                );
        Cursor c = db.rawQuery (dbQuery, null);

        int i = 0;
        while (c.moveToNext())
        {
            if (i >= start) {

                ProjectSettings settings;

                //int id = c.getInt ( c.getColumnIndex ( Constants.COLUMN_ID ) );
                byte[] data = c.getBlob ( c.getColumnIndex ( Constants.COLUMN_DATA ) );
                ByteArrayInputStream inputStream = new ByteArrayInputStream ( data );
                ObjectInput input;
                try {
                    input = new ObjectInputStream ( inputStream );
                    settings = (ProjectSettings) input.readObject ( );
                } catch (Exception e) {
                    e.printStackTrace ( );
                    Log.d ( "Developer", e.toString ( ) );
                    continue;
                }

                if (settings != null)
                    ret.add ( settings );
            }
            i++;
        }
        c.close();

        return ret;
    }

    public List<ProjectSettings> toList(int start, int end, String tableName) {
        return toList(start, end, tableName, Constants.COLUMN_CREATED_DATE);
    }

    public boolean hasOccurrence(ProjectSettings projectSettings, String tableName) {
        SQLiteDatabase database = getReadableDatabase ();
        String sql = String.format ( "SELECT * FROM %s WHERE %s=\"%s\" AND %s=\"%s\" LIMIT 1",
                tableName,
                Constants.COLUMN_USERNAME,
                projectSettings.getUserData ().getUsername (),
                Constants.COLUMN_TITLE,
                projectSettings.getTitle ()
        );
        Cursor cursor = database.rawQuery ( sql, null );
        return cursor.getCount () == 1;
    }

    public boolean addProject(ProjectSettings projectSettings, long id, String tableName) {
        boolean ret = false;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream ( bos );
            out.writeObject ( projectSettings );
            out.flush ( );
            bos.close ( );
        }
        catch (IOException e) {
            Log.d ( "Developer", e.toString () );
            return false;
        }

        try{
            String sql = "";
            if (tableName == Constants.PRIVATE_TABLE_NAME) {
                sql = String.format ( "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES(?, ?, ?, ?, ?)",
                        tableName,
                        Constants.COLUMN_ID,
                        Constants.COLUMN_DATA,
                        Constants.COLUMN_CREATED_DATE,
                        Constants.COLUMN_USERNAME,
                        Constants.COLUMN_TITLE
                );
            }
            else if (tableName == Constants.PUBLIC_TABLE_NAME) {
                sql = String.format ( "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES(?, ?, ?, ?, ?, ?, ?)",
                        tableName,
                        Constants.COLUMN_ID,
                        Constants.COLUMN_DATA,
                        Constants.COLUMN_CREATED_DATE,
                        Constants.COLUMN_USERNAME,
                        Constants.COLUMN_TITLE,
                        Constants.COLUMN_PUBLIC_VIEWS,
                        Constants.COLUMN_PUBLIC_RATING
                );
            }

            SQLiteStatement insertStmt =  db.compileStatement(sql);
            insertStmt.clearBindings();
            insertStmt.bindLong(1, id);
            insertStmt.bindBlob(2, bos.toByteArray());
            insertStmt.bindLong (3, projectSettings.getLastUpdated ().getTime ());
            insertStmt.bindString (4, projectSettings.getUserData ().getUsername ());
            insertStmt.bindString (5, projectSettings.getTitle ());
            if (tableName == Constants.PUBLIC_TABLE_NAME) {
                insertStmt.bindLong (6, projectSettings.getViews ());
                insertStmt.bindDouble (7, projectSettings.getRatings ());
            }
            insertStmt.executeInsert();

            db.setTransactionSuccessful();
            db.endTransaction();

            ret = true;
        }
        catch(Exception e){
            e.printStackTrace();
            Log.d ( "Developer", e.toString () );
            ret = false;
        }

        return ret;
    }

    public long getMaxId(String tableName){
        String selectQuery = String.format ( "SELECT * FROM %s ORDER BY %s DESC",
                tableName,
                Constants.COLUMN_ID
                );

        SQLiteDatabase database = this.getReadableDatabase ();

        Cursor cursor = database.rawQuery(selectQuery, null);

        long maxId;
        if (cursor != null && cursor.getCount () > 0 && cursor.moveToFirst ())
            maxId = cursor.getLong (cursor.getColumnIndex(Constants.COLUMN_ID));
        else
            maxId = -1;

        cursor.close ();

        return maxId;
    }

}
