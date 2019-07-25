package templeofmaat.judgment;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import templeofmaat.judgment.data.Category;


public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = CategoryActivity.class.getName();

    CategoryService categoryService;
    private ArrayAdapter categoryListAdapter;
    private ListView categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setTitle("Categories");

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
                // If User  Picked New
                if (position == 0) {
                    createNewCategory();
                } else {
                    Intent intent = new Intent(CategoryActivity.this, CategoryPickedActivity.class);
                    intent.putExtra("Category", itemValue);
                    startActivity(intent);
                }
            }
        });
    }

    public void createNewCategory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        builder.setTitle("New Category");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCategory = input.getText().toString();
                new AsyncTaskInsert(CategoryActivity.this).execute(new Category(newCategory));
                populate();
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

    public void populate(){
        categoryList = findViewById(R.id.categoryList);
        final LiveData<List<String>> categories = categoryService.getAllLabels();
        categories.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> loadedCategories) {
                List<String> categories = new ArrayList<>();

                categories.add("New");
                if (loadedCategories != null) {
                    categories.addAll(loadedCategories);
                }
                categoryListAdapter = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.mytextview, R.id.textview_1, categories);
                categoryList.setAdapter(categoryListAdapter);

            }
        });
    }

    public void initialize(){
        new AsyncTaskInsert(CategoryActivity.this).execute(new Category("Restaurants"));
        new AsyncTaskInsert(CategoryActivity.this).execute(new Category("Books"));
        populate();
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
            } catch (SQLiteConstraintException exception) {
                Log.i(TAG, "Category already exists", exception);
                return false;
            }

            Log.i(TAG, "Created new category: " + category[0].getName());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                CategoryActivity categoryActivity = categoryActivityWeakReference.get();
                if (categoryActivity != null) {
                    Toast.makeText(categoryActivity,
                            "Category already exists", Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    }

}
