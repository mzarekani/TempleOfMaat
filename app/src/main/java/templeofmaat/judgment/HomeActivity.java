package templeofmaat.judgment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.lang.ref.WeakReference;

import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.CategoryReview;
import templeofmaat.judgment.data.CategoryReviewDao;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getName();

    private CategoryReviewDao categoryReviewDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        categoryReviewDao = AppDatabase.getAppDatabase(this).categoryReviewDao();
    }

    public void loadExistingUserOrInitNewUser(View view) {
        if (getDatabasePath(AppDatabase.DATABASE_NAME).exists()) {
            loadRootCategoryReview();
        } else {
            initializeNewUser();
        }
    }

    private void loadRootCategoryReview() {
        LiveData<CategoryReview> liveRootCategory = categoryReviewDao.getCategoryReview(1);
        liveRootCategory.observe(this, new Observer<CategoryReview>() {
            @Override
            public void onChanged(@Nullable CategoryReview rootCategoryReview) {
                if (rootCategoryReview != null) {
                    startCategoryReviewActivity(rootCategoryReview);
                }
            }
        });
    }

    private void initializeNewUser() {
        Log.i(TAG, "Initializing new user");
        CategoryReview rootCategoryReview = new CategoryReview("Categories", null, true, false, null);
        rootCategoryReview.setId(1);
        new AsyncTaskInsertCategory(this).execute(rootCategoryReview);
        new AsyncTaskInsertCategory(this).execute(new CategoryReview("Books", 1, true, false, null));
        startCategoryReviewActivity(rootCategoryReview);
    }

    private void startCategoryReviewActivity(CategoryReview categoryReview) {
        Intent intent = new Intent(HomeActivity.this, CategoryReviewActivity.class);
        intent.putExtra(Constants.CATEGORY_REVIEW, categoryReview);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    private static class AsyncTaskInsertCategory extends AsyncTask<CategoryReview, Void, Boolean> {
        private WeakReference<HomeActivity> homeActivityWeakReference;

        private AsyncTaskInsertCategory(HomeActivity homeActivity) {
            this.homeActivityWeakReference = new WeakReference<>(homeActivity);
        }

        @Override
        protected Boolean doInBackground(CategoryReview... categoryReview) {
            try {
                homeActivityWeakReference.get().categoryReviewDao.insert(categoryReview[0]);
            } catch (SQLiteException exception) {
                Log.e(TAG, "Error Creating New Category", exception);
                return false;
            }

            Log.i(TAG, "Created new category: " + categoryReview[0].getTitle());
            return true;
        }
    }
}
