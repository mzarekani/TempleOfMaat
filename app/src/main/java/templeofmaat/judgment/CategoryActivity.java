package templeofmaat.judgment;

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


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import templeofmaat.judgment.data.Category;
import templeofmaat.judgment.data.ReviewEssentials;


public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = CategoryActivity.class.getName();

    CategoryService categoryService;
    private ArrayAdapter categoryListAdapter;
    private ListView categoryList;
    ArrayList<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Categories");

        categoryList = findViewById(R.id.categoryList);

        categoryService = new CategoryService(this);

        // Revisit when allow for multiple accounts
//        SharedPreferences sharedPref = this.getSharedPreferences("user_info", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("user_account", accountName);
//        editor.apply();
//
//        accountName = sharedPref.getString("user_account", "none");
        if (categoryService.doesDatabaseExist()){
            populate();
        } else {
            Log.i(TAG, "Creating database for new user");
            initialize();
        }

        addOnItemClickListener();
    }

    public void addOnItemClickListener() {
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                String itemValue = (String) categoryList.getItemAtPosition(position);
                Intent intent = new Intent(CategoryActivity.this, CategoryPickedActivity.class);
                intent.putExtra("Category", itemValue);
                startActivity(intent);
            }
        });
    }

    public void populate(){
        final LiveData<List<String>> liveCategories = categoryService.getAllLabels();
        liveCategories.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> loadedCategories) {
                if (loadedCategories != null) {
                    categories = new ArrayList<>();
                    categories.addAll(loadedCategories);
                    categoryListAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.mytextview, R.id.textview_1, categories);
                    categoryList.setAdapter(categoryListAdapter);
                }
            }
        });
    }

    public void initialize(){
        new AsyncTaskInsert(CategoryActivity.this).execute(new Category("Restaurants", CategoryTypes.REVIEW.getDisplayName()));
        new AsyncTaskInsert(CategoryActivity.this).execute(new Category("Books", CategoryTypes.REVIEW.getDisplayName()));
        populate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);
        menu.add(getString(R.string.category_new));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getTitle().equals(getString(R.string.category_new))) {
            Intent intent = new Intent(CategoryActivity.this, EditCategoryActivity.class);
            intent.putStringArrayListExtra("categories", categories);
            startActivity(intent);
        }

        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        populate();
    }

    private static class AsyncTaskInsert extends AsyncTask<Category, Void, Boolean> {
        private WeakReference<CategoryActivity> categoryActivityWeakReference;

        private AsyncTaskInsert(CategoryActivity categoryActivity) {
            this.categoryActivityWeakReference = new WeakReference<>(categoryActivity);
        }

        @Override
        protected Boolean doInBackground(Category... category) {
            try {
                categoryActivityWeakReference.get().categoryService.insertCategory(category[0]);
            } catch (SQLiteException exception) {
                Log.e(TAG, "Error Creating New Category", exception);
                return false;
            }

            Log.i(TAG, "Created new category: " + category[0].getName());
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
