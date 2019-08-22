package templeofmaat.judgment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.CategoryReview;
import templeofmaat.judgment.data.CategoryReviewDao;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = CategoryActivity.class.getName();

    CategoryService categoryService;
    private ArrayAdapter categoryListAdapter;
    private ListView categoryList;
    ArrayList<CategoryReview> categories;
    private CategoryReviewDao categoryReviewDao;
    CategoryReview categoryReview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null && extras.containsKey("CategoryReview")) {
            categoryReview = (CategoryReview) extras.getSerializable("CategoryReview");
            setTitle(categoryReview.getTitle());
        } else {
            setTitle("Categories");
        }

        categoryService = new CategoryService(this);

        // Revisit when allow for multiple accounts
//        SharedPreferences sharedPref = this.getSharedPreferences("user_info", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("user_account", accountName);
//        editor.apply();
//
//        accountName = sharedPref.getString("user_account", "none");
        categoryReviewDao = AppDatabase.getAppDatabase(this).categoryReviewDao();
        categoryList = findViewById(R.id.categoryList);
        if (categoryService.doesDatabaseExist()){
            populate();
        } else {
            Log.i(TAG, "Creating database for new user");
            initialize();
        }

        addOnItemClickListener();
    }

    public void populate(){
        LiveData<List<CategoryReview>> liveCategoryReviews;
        if (categoryReview != null) {
            liveCategoryReviews = categoryReviewDao.getReviewCategoriesForParent(categoryReview.getId());
        } else {
            liveCategoryReviews = categoryReviewDao.getRootReviewCategories();
        }
        liveCategoryReviews.observe(this, new Observer<List<CategoryReview>>() {
            @Override
            public void onChanged(@Nullable List<CategoryReview> loadedCategoryReviews) {
                if (loadedCategoryReviews != null) {
                    categories = new ArrayList<>(loadedCategoryReviews);
                    categoryListAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.mytextview, R.id.textview_1, categories);
                    categoryList.setAdapter(categoryListAdapter);
                }
            }
        });
    }

    public void initialize(){
       // new AsyncTaskInsert(CategoryActivity.this).execute(new Category("Restaurants", CategoryType.REVIEW.getDisplayName()));
      //  new AsyncTaskInsert(CategoryActivity.this).execute(new Category("Books", CategoryType.REVIEW.getDisplayName()));

     //   new AsyncTaskInsert(CategoryActivity.this).execute(new CategoryReview("Restaurants", CategoryType.REVIEW.getDisplayName()));
        new AsyncTaskInsert(CategoryActivity.this).execute(new CategoryReview("Books", null, true, false, null));
        populate();
    }

    public void addOnItemClickListener() {
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
             //   String itemValue = (String) categoryList.getItemAtPosition(position);
                Intent intent = new Intent(CategoryActivity.this, CategoryActivity.class);
                CategoryReview categoryReview = (CategoryReview) categoryList.getItemAtPosition(position) ;
                intent.putExtra("CategoryReview", categoryReview);
                //intent.putExtra("Category", itemValue);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);
        menu.add(getString(R.string.category_new));
        if (categoryReview != null) {
            menu.add(getString(R.string.category_edit));
            menu.add(getString(R.string.category_delete));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        CharSequence selected = item.getTitle();
        if (selected.equals(getString(R.string.category_new))) {
            Intent intent = new Intent(CategoryActivity.this, EditCategoryActivity.class);
            startActivity(intent);
        } else if (selected.equals(getString(R.string.category_edit))) {
            Intent intent = new Intent(CategoryActivity.this, EditCategoryActivity.class);
            intent.putExtra("CategoryReview", categoryReview);
            startActivity(intent);
        } else if (selected.equals(getString(R.string.category_delete))) {
            confirmDeleteCategoryReview();
        }

        return true;
    }

    private void confirmDeleteCategoryReview() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,  R.style.AlertDialog);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to delete " + categoryReview.getTitle() + "? " +
                "Your reviews/sub-categories for this category will be lost.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCategoryReview();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void deleteCategoryReview() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                categoryReviewDao.delete(categoryReview);
            }
        });
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        populate();
    }

    private static class AsyncTaskInsert extends AsyncTask<CategoryReview, Void, Boolean> {
        private WeakReference<CategoryActivity> categoryActivityWeakReference;

        private AsyncTaskInsert(CategoryActivity categoryActivity) {
            this.categoryActivityWeakReference = new WeakReference<>(categoryActivity);
        }

        @Override
        protected Boolean doInBackground(CategoryReview... categoryReview) {
            try {
             //   categoryActivityWeakReference.get().categoryService.insertCategory(category[0]);
                categoryActivityWeakReference.get().categoryReviewDao.insert(categoryReview[0]);
            } catch (SQLiteException exception) {
                Log.e(TAG, "Error Creating New Category", exception);
                return false;
            }

            Log.i(TAG, "Created new category: " + categoryReview[0].getTitle());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                CategoryActivity categoryActivity = categoryActivityWeakReference.get();
                if (categoryActivity != null) {
                    categoryActivity.populate();
                }
            }
        }
    }

}
