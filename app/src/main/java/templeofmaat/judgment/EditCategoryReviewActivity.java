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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.Book;
import templeofmaat.judgment.data.BookDao;
import templeofmaat.judgment.data.CategoryReview;
import templeofmaat.judgment.data.CategoryReviewDao;

public class EditCategoryReviewActivity extends AppCompatActivity {

    private static final String TAG = EditCategoryReviewActivity.class.getName();

    private CategoryReviewDao categoryReviewDao;
    private BookDao bookDao;
    private TextView nameView;
    private TextInputEditText commentView;
    private RatingBar ratingBar;
    private Spinner reviewTypeSpinner;
    private List<ReviewType> reviewTypes;
    RadioGroup categoryReviewType;
    private CategoryReview categoryReview;
    private Integer parentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        nameView = findViewById(R.id.title);
        ratingBar = findViewById(R.id.rating_bar);
        commentView = findViewById(R.id.comments);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("category_review")) {
                categoryReview = (CategoryReview) extras.getSerializable("category_review");
                setTitle(categoryReview.getTitle());
            } else if (extras.containsKey("parent_id")){
                parentId = extras.getInt("parent_id");
            }
        } else {
            setTitle("New Entry");
        }

        categoryReviewDao = AppDatabase.getAppDatabase(this).categoryReviewDao();
        bookDao = AppDatabase.getAppDatabase(this).bookDao();

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
        categoryReviewType = findViewById(R.id.radio_group_category_review_type);
        categoryReviewType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton checkedRadioButton = radioGroup.findViewById(checkedId);
                boolean checked = checkedRadioButton.isChecked();

                switch(checkedId) {
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
        });
    }

    public void onSave() {
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String title = nameView.getText().toString().trim();
                if(!validateTitle(title)) {
                    return;
                }

                CategoryReview newCategoryReview;
                ReviewType selected = (ReviewType) reviewTypeSpinner.getSelectedItem();
                RadioGroup categoryReviewType = findViewById(R.id.radio_group_category_review_type);
//                switch (categoryReviewType.getCheckedRadioButtonId()) {
//                    case R.id.radio_category:
//                        if (validateTitle(title)) {
//                            new AsyncTaskInsert(EditCategoryReviewActivity.this).
//                                    execute(new CategoryReview(title, parentId, true, false, null));
//                        }
//                        break;
//                    case R.id.radio_review:
//                        if (selected == ReviewType.SELECT) {
//                            Toast.makeText(EditCategoryReviewActivity.this,
//                                    "Must pick a type", Toast.LENGTH_LONG)
//                                    .show();
//                        } else if (selected == ReviewType.Book) {
//                            new AsyncTaskInsert(EditCategoryReviewActivity.this).
//                                    execute(new CategoryReview(title, parentId, false, true, selected.toString()), new Book());
//                        }
//                        break;
//                    case R.id.radio_category_review:
//                        new AsyncTaskInsert(EditCategoryReviewActivity.this).
//                                execute(new CategoryReview(title, parentId, true, true, selected.toString()), new Book());
//                        break;
//                }

                if (categoryReviewType.getCheckedRadioButtonId() == R.id.radio_category) {
                    new AsyncTaskInsert(EditCategoryReviewActivity.this).
                            execute(new CategoryReview(title, parentId, true, false, null));
                } else {
                    boolean isCategoryReview = categoryReviewType.getCheckedRadioButtonId() == R.id.radio_category_review;
                    if (selected == ReviewType.SELECT) {
                        Toast.makeText(EditCategoryReviewActivity.this,
                                "Must pick a type", Toast.LENGTH_LONG)
                                .show();
                    } else if (selected == ReviewType.Book) {
                        newCategoryReview = new CategoryReview(title, parentId, isCategoryReview, true, selected.toString());
                        Book newBook = new Book(ratingBar.getRating(), commentView.getText().toString(), null);
                        new AsyncTaskInsert(EditCategoryReviewActivity.this).
                                execute(newCategoryReview, newBook);
                    }
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

    private static class AsyncTaskInsert extends AsyncTask<Object, Void, Boolean> {
        private WeakReference<EditCategoryReviewActivity> editCategoryActivityWeakReference;

        private AsyncTaskInsert(EditCategoryReviewActivity editCategoryReviewActivity) {
            this.editCategoryActivityWeakReference = new WeakReference<>(editCategoryReviewActivity);
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            CategoryReview categoryReview = (CategoryReview) objects[0];
            long id;
            try {
                id = editCategoryActivityWeakReference.get().categoryReviewDao.insert(categoryReview);
            } catch (SQLiteException exception) {
                Log.e(TAG, "Error Creating New Category", exception);
                return false;
            }

            if (categoryReview.isReview()) {
                if (categoryReview.getReviewType().equals(ReviewType.Book.toString())) {
                    Book book = (Book) objects[1];
                    book.setCategoryReviewId((int)id);
                    editCategoryActivityWeakReference.get().bookDao.insert(book);
                }
            }

            Log.i(TAG, "Created new category: " + categoryReview.getTitle());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                EditCategoryReviewActivity editCategoryReviewActivity = editCategoryActivityWeakReference.get();
                if (editCategoryReviewActivity != null) {
                    editCategoryReviewActivity.finish();
                }
            }
        }
    }

}
