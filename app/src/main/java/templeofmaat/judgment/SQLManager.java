package templeofmaat.judgment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class SQLManager extends SQLiteOpenHelper {
    Context context;
    private static final String DATABASE_NAME = "Judgement.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;

    private static class TableEntry implements BaseColumns {
        private static final String TABLE_NAME = "cateogires";
        private static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_STARS = "stars";
        public static final String COLUMN_NAME_REVIEW = "review";
    }

    private String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TableEntry.TABLE_NAME + " (" +
                    TableEntry._ID + " INTEGER PRIMARY KEY," +
                    TableEntry.COLUMN_NAME_TITLE + " TEXT)";

    SQLManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(SQL_DELETE_ENTRIES);
//        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
