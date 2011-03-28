package se.ekonomipuls;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Helper class for dealing with database
 *
 * @author Michael Svensson
 */
public class DBAdapter {

    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "Databasnamn";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "create table blah blah..";

    private InternalDatabaseHelper internalDatabaseHelper;
    private SQLiteDatabase db;

    @Inject
    private static Provider<Context> contextProvider;

    public DBAdapter() {
        internalDatabaseHelper = new InternalDatabaseHelper(contextProvider.get());
    }

    private static class InternalDatabaseHelper extends SQLiteOpenHelper {

        private InternalDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException {
        db = internalDatabaseHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close() {
        internalDatabaseHelper.close();
    }

    //---insert a title into the database---
    public long insertTitle(String isbn, String title, String publisher) {
        ContentValues initialValues = new ContentValues();
        //initialValues.put(KEY_ISBN, isbn);
        //initialValues.put(KEY_TITLE, title);
        //initialValues.put(KEY_PUBLISHER, publisher);
        //return db.insert(DATABASE_TABLE, null, initialValues);
        return 0;
    }


}
