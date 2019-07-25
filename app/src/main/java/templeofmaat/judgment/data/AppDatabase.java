package templeofmaat.judgment.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Category.class, Review.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "user";

    private static AppDatabase INSTANCE;

    public abstract CategoryDao categoryDao();
    public abstract ReviewDao reviewDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
