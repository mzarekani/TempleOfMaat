package templeofmaat.judgment;

import android.content.Context;
import java.util.List;
import androidx.lifecycle.LiveData;

import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.Category;

class CategoryService {

    private Context context;
    private AppDatabase db;

    CategoryService(Context context) {
        this.context = context;
        db = AppDatabase.getAppDatabase(context);
    }

    LiveData<List<Category>> getAllCategories() {
        return db.categoryDao().getAllCategories();
    }

    LiveData<List<String>> getAllLabels() {
        return db.categoryDao().getAllLabels();
    }

    void insertCategory(final Category category) {
        db.categoryDao().insert(category);
    }

    boolean doesDatabaseExist() {
        return context.getDatabasePath(AppDatabase.DATABASE_NAME).exists();
    }
}
