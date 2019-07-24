package templeofmaat.judgment;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import templeofmaat.judgment.data.Category;

public class CategoryPickedService {

    private AppDatabase db;
    private Context context;

    CategoryPickedService(Context context) {
        this.context = context;
        db = Room.databaseBuilder(context,
                AppDatabase.class, "lykus").build();
    }

    LiveData<Category> getCategory(int id) {
        return db.categoryDao().getCategory(id);
    }

    LiveData<Category> getCategory(String name) {
        return db.categoryDao().getCategory(name);
    }

    LiveData<List<String>> getAllNamesForCategory(int categoryId) {
        return db.reviewDao().getAllNamesForCategory(categoryId);
    }

    void deleteCategory(final Category category) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db.categoryDao().delete(category);
            }
        });
    }

}
