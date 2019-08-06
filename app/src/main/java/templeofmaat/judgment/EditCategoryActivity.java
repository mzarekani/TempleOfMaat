package templeofmaat.judgment;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.Category;
import templeofmaat.judgment.data.CategoryDao;

public class EditCategoryActivity extends AppCompatActivity {

    private static final String TAG = EditCategoryActivity.class.getName();

    private CategoryDao categoryDao;
    private TextView nameView;
    private Spinner categoryTypeSpinner;
    private List categories;
    private List<String> categoryTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        nameView = findViewById(R.id._name);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            categories = extras.getStringArrayList("categories");
        }

        categoryDao = AppDatabase.getAppDatabase(this).categoryDao();

        setUpAdapter();
        setUpListeners();
    }

    private void setUpAdapter() {
        categoryTypes = Stream.of(CategoryTypes.values())
                .map(CategoryTypes::getDisplayName)
                .collect(Collectors.toList());
        categoryTypeSpinner = findViewById(R.id.selectTypeSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  R.layout.spinner_text_view, categoryTypes) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = super.getDropDownView(position, convertView, parent);
                convertView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams p = convertView.getLayoutParams();
                p.height = 100;
                convertView.setLayoutParams(p);

                return convertView;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryTypeSpinner.setAdapter(adapter);
    }

    public void setUpListeners() {
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = nameView.getText().toString().trim();
                String categoryType = (String) categoryTypeSpinner.getSelectedItem();
                if (validateNewCategory(categoryType) && validateName(name)) {
                    new AsyncTaskInsert(EditCategoryActivity.this).execute(new Category(name, categoryType));
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean validateNewCategory(String categoryType) {
        boolean categoryValid = false;
        if (categoryTypeSpinner.getSelectedItemId() == 0) {
            Toast.makeText(this,
                    "Must pick a type", Toast.LENGTH_LONG)
                    .show();
        } else if (!categoryTypes.contains(categoryType)) {
            Toast.makeText(this,
                    "Type Not allowed", Toast.LENGTH_LONG)
                    .show();
        } else {
            categoryValid = true;
        }
        return categoryValid;
    }

    private boolean validateName(String newCategory) {
        boolean newNameValid = false;
        if (newCategory.isEmpty()) {
            Toast.makeText(this,
                    "Name can't be empty", Toast.LENGTH_LONG)
                    .show();
        } else if (categories.contains(newCategory)) {
            Toast.makeText(this,
                    "Category already exists", Toast.LENGTH_LONG)
                    .show();
        } else {
            newNameValid = true;
        }
        return newNameValid;
    }

    private static class AsyncTaskInsert extends AsyncTask<Category, Void, Boolean> {
        private WeakReference<EditCategoryActivity> editCategoryActivityWeakReference;

        private AsyncTaskInsert(EditCategoryActivity editCategoryActivity) {
            this.editCategoryActivityWeakReference = new WeakReference<>(editCategoryActivity);
        }

        @Override
        protected Boolean doInBackground(Category... category) {
            try {
                editCategoryActivityWeakReference.get().categoryDao.insert(category[0]);
            } catch (SQLiteException exception) {
                Log.e(TAG, "Error Creating New Category", exception);
                return false;
            }

            Log.i(TAG, "Created new category: " + category[0].getName());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                EditCategoryActivity editCategoryActivity = editCategoryActivityWeakReference.get();
                if (editCategoryActivity != null) {
                    editCategoryActivity.finish();
                }
            }
        }
    }

}
