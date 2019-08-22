package templeofmaat.judgment;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.CategoryReview;
import templeofmaat.judgment.data.CategoryReviewDao;

public class EditCategoryActivity extends AppCompatActivity {

    private static final String TAG = EditCategoryActivity.class.getName();

    private CategoryReviewDao categoryReviewDao;
    private TextView nameView;
    private RatingBar ratingBar;
    private Spinner reviewTypeSpinner;
    private List<ReviewType> reviewTypes;
    private CategoryReview categoryReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        nameView = findViewById(R.id._name);
        ratingBar = findViewById(R.id.rating_bar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey("CategoryReview")) {
            categoryReview = (CategoryReview) extras.getSerializable("CategoryReview");
            setTitle(categoryReview.getTitle());
        } else {
            setTitle("New Entry");
        }

        categoryReviewDao = AppDatabase.getAppDatabase(this).categoryReviewDao();

        setUpAdapter();
        setUpListeners();
    }

    private void setUpAdapter() {
        reviewTypes = new ArrayList<>(EnumSet.allOf(ReviewType.class));
        ArrayAdapter<ReviewType> adapter = new ArrayAdapter<ReviewType>(this,  R.layout.spinner_text_view, reviewTypes) {
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
        reviewTypeSpinner = findViewById(R.id.selectTypeSpinner);
        reviewTypeSpinner.setAdapter(adapter);
        reviewTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ReviewType selected =  (ReviewType) adapterView.getSelectedItem();
                TextInputLayout textInputLayout = findViewById(R.id.text_input_layout_comments);
                if (selected == ReviewType.Book) {
                    ratingBar.setVisibility(View.VISIBLE);
                    textInputLayout.setVisibility(View.VISIBLE);
                } else {
                    ratingBar.setVisibility(View.GONE);
                    textInputLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setUpListeners() {
        onSave();
        onCancel();
    }

    public void onSave() {
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String title = nameView.getText().toString().trim();
                if(!validateTitle(title)) {

                }
                Integer parentId = null;
                if (categoryReview != null) {
                    parentId = categoryReview.getId();
                }

                RadioGroup categoryReviewType = findViewById(R.id.radio_group_category_review_type);
                switch (categoryReviewType.getCheckedRadioButtonId()) {
                    case R.id.radio_category:
                        if (validateTitle(title)) {
                            new AsyncTaskInsert(EditCategoryActivity.this).
                                    execute(new CategoryReview(title, parentId, true, false, null));
                            finish();
                        }
                        break;
                    case R.id.radio_review:
                        ReviewType selected = (ReviewType) reviewTypeSpinner.getSelectedItem();
                        if (selected == ReviewType.SELECT) {
                            Toast.makeText(EditCategoryActivity.this,
                                    "Must pick a type", Toast.LENGTH_LONG)
                                    .show();
                        }
                        if (selected == ReviewType.Book) {
                            new AsyncTaskInsert(EditCategoryActivity.this).
                                    execute(new CategoryReview(title, parentId, false, true, selected.toString()));
                            finish();
                        }
                        break;
                    case R.id.radio_category_review:
                        break;
                }
            }
        });
    }

    public void onCancel() {
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean validateNewReview() {
        boolean categoryValid = false;
        if (reviewTypeSpinner.getSelectedItemId() == 0) {
            Toast.makeText(this,
                    "Must pick a type", Toast.LENGTH_LONG)
                    .show();
        } else {
            categoryValid = true;
        }
        return categoryValid;
    }

    private boolean validateTitle(String title) {
        boolean titleValid = false;
        if (title.isEmpty()) {
            Toast.makeText(this,
                    "Title can't be empty", Toast.LENGTH_LONG)
                    .show();
        } else {
            titleValid = true;
        }
        return titleValid;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_category:
                if (checked) {
                    reviewTypeSpinner.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.radio_review:
                if (checked) {
                    reviewTypeSpinner.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.radio_category_review:
                if (checked) {
                    reviewTypeSpinner.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private static class AsyncTaskInsert extends AsyncTask<CategoryReview, Void, Boolean> {
        private WeakReference<EditCategoryActivity> editCategoryActivityWeakReference;

        private AsyncTaskInsert(EditCategoryActivity editCategoryActivity) {
            this.editCategoryActivityWeakReference = new WeakReference<>(editCategoryActivity);
        }

        @Override
        protected Boolean doInBackground(CategoryReview... categoryReview) {
            try {
                editCategoryActivityWeakReference.get().categoryReviewDao.insert(categoryReview[0]);
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
                EditCategoryActivity editCategoryActivity = editCategoryActivityWeakReference.get();
                if (editCategoryActivity != null) {
                    editCategoryActivity.finish();
                }
            }
        }
    }

}
