//package templeofmaat.judgment;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//import java.util.List;
//
//import androidx.lifecycle.LiveData;
//
//import templeofmaat.judgment.data.AppDatabase;
//import templeofmaat.judgment.data.Category;
//
//class CategoryPickedService {
//
//    private AppDatabase db;
//
//    CategoryPickedService(Context context) {
//        db = AppDatabase.getAppDatabase(context);
//    }
//
//    LiveData<Category> getCategory(int id) {
//        return db.categoryDao().getCategory(id);
//    }
//
//    LiveData<Category> getCategory(String name) {
//        return db.categoryDao().getCategory(name);
//    }
//
//    LiveData<List<String>> getAllNamesForCategory(int categoryId) {
//        return db.reviewDao().getAllNamesForCategory(categoryId);
//    }
//
//    void deleteCategory(final Category category) {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                db.categoryDao().delete(category);
//            }
//        });
//    }
//
//}
