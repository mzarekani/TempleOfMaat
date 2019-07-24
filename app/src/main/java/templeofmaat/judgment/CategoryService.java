package templeofmaat.judgment;

import android.content.Context;
import android.os.AsyncTask;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import templeofmaat.judgment.data.Category;

public class CategoryService {

    private AppDatabase db;
    private Context context;

    CategoryService(Context context) {
        this.context = context;
        db = Room.databaseBuilder(context,
                AppDatabase.class, "lykus").build();
    }

    LiveData<List<Category>> getAllCategories() {
        return db.categoryDao().getAllCategories();
    }

    LiveData<List<String>> getAllLabels() {
        return db.categoryDao().getAllLabels();
    }

    void insertCategory(final Category category) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db.categoryDao().insert(category);
            }
        });
    }

}
