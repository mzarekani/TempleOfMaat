package templeofmaat.judgment;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;
import java.util.List;

import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.Book;
import templeofmaat.judgment.data.BookDao;
import templeofmaat.judgment.data.CategoryReview;
import templeofmaat.judgment.data.CategoryReviewDao;

public class EditCategoryReviewActivity extends AppCompatActivity implements CategoryReviewFragment.OnFragmentInteractionListener {

    private static final String TAG = EditCategoryReviewActivity.class.getName();

    private CategoryReviewDao categoryReviewDao;
    private BookDao bookDao;
    private CategoryReview categoryReview;
    private Integer parentId;
    CategoryReviewFragment categoryReviewFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

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

        setUpListeners();

        if (categoryReview != null && categoryReview.isReview() && savedInstanceState == null) {
            categoryReviewFragment = CategoryReviewFragment.newInstance(categoryReview, null, true);
            getSupportFragmentManager().beginTransaction().add(R.id.category_review_fragment, categoryReviewFragment).commit();
        } else if (parentId != null && savedInstanceState == null) {
            categoryReviewFragment = CategoryReviewFragment.newInstance(null, parentId, true);
            getSupportFragmentManager().beginTransaction().add(R.id.category_review_fragment, categoryReviewFragment).commit();
        }

    }

    public void setUpListeners() {
        onSave();
        onCancel();
    }

    public void onSave() {
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                categoryReviewFragment.save();
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
//        if (reviewTypeSpinner.getSelectedItemId() == 0) {
//            Toast.makeText(this,
//                    "Must pick a type", Toast.LENGTH_LONG)
//                    .show();
//        } else {
//            categoryValid = true;
//        }
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

    @Override
    public void finishActivity(){
        finish();
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
