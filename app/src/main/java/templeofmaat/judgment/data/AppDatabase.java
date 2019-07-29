package templeofmaat.judgment.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Category.class, Review.class}, version = AppDatabase.VERSION_NUMBER)
public abstract class AppDatabase extends RoomDatabase {
    static final int VERSION_NUMBER = 2;
    public static final String DATABASE_NAME = "user";

    private static AppDatabase INSTANCE;

    public abstract CategoryDao categoryDao();
    public abstract ReviewDao reviewDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            .addMigrations(MIGRATION_1_2)
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE INDEX IF NOT EXISTS index_review_name ON review (name) ");
        }
    };
}
