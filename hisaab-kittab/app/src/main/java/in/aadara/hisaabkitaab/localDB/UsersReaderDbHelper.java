package in.aadara.hisaabkitaab.localDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by umashankarpathak on 15/01/18.
 */

public class UsersReaderDbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UsersReaderContract.FeedEntry.TABLE_NAME + " (" +
                    UsersReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    UsersReaderContract.FeedEntry.COLUMN_AMOUNT + " TEXT," +
                    UsersReaderContract.FeedEntry.COLUMN_DATE + " TEXT," +
                    UsersReaderContract.FeedEntry.COLUMN_MOBILE + " TEXT," +
                    UsersReaderContract.FeedEntry.COLUMN_NAME + " TEXT," +
                    UsersReaderContract.FeedEntry.COLUMN_ADDRESS + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UsersReaderContract.FeedEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public UsersReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
