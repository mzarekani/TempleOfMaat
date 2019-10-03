package templeofmaat.judgment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;


import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.CategoryReview;
import templeofmaat.judgment.data.CategoryReviewDao;

public class CategoryActivity extends AppCompatActivity implements CategoryReviewFragment.OnFragmentInteractionListener {
    private static final String TAG = CategoryActivity.class.getName();

    private static final String CATEGORY_REVIEW = "category_review";

    private ArrayAdapter categoryReviewAdapter;
    private ListView categoryReviewListView;
    ArrayList<CategoryReview> categoryReviews;
    private CategoryReviewDao categoryReviewDao;
    CategoryReview categoryReview;
    FrameLayout frameLayout;
    float fragmentBottom;
    CategoryReviewFragment categoryReviewFragment;
    FragmentTransaction fragmentTransaction;
    private Toolbar toolbar;
    int fragmentYDelta = 0;
    Integer originalFragmentY;
    View categoryReviewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.category_review_fragment);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null && extras.containsKey(CATEGORY_REVIEW)) {
            categoryReview = (CategoryReview) extras.getSerializable(CATEGORY_REVIEW);
            setTitle(categoryReview.getTitle());
        } else {
            setTitle("Categories");
        }

        loadFragment();

        // TODO Revisit when allow for multiple accounts
//        SharedPreferences sharedPref = this.getSharedPreferences("user_info", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("user_account", accountName);
//        editor.apply();
//
//        accountName = sharedPref.getString("user_account", "none");

        categoryReviewDao = AppDatabase.getAppDatabase(this).categoryReviewDao();
        categoryReviewListView = findViewById(R.id.categoryList);
        if (getDatabasePath(AppDatabase.DATABASE_NAME).exists()){
            populate();
        } else {
            Log.i(TAG, "Creating database for new user");
            initialize();
        }

        addListeners();

    }

    private void loadFragment() {
        if (categoryReview != null && categoryReview.isReview()) {
            categoryReviewFragment = CategoryReviewFragment.newInstance(categoryReview, null,false);
            getSupportFragmentManager().beginTransaction().add(R.id.category_review_fragment, categoryReviewFragment).commit();
        }
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
                    categoryReviews = new ArrayList<>(loadedCategoryReviews);
                    categoryReviewAdapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.mytextview, R.id.textview_1, categoryReviews);
                    categoryReviewListView.setAdapter(categoryReviewAdapter);
                }
            }
        });
    }

    public void initialize(){
        new AsyncTaskInsert(CategoryActivity.this).execute(new CategoryReview("Books", null, true, false, null));
        populate();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addListeners() {
        categoryReviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Intent intent;
                CategoryReview categoryReview = (CategoryReview) categoryReviewListView.getItemAtPosition(position);
                if (categoryReview.isCategory()) {
                    intent = new Intent(CategoryActivity.this, CategoryActivity.class);
                } else {
                    intent = new Intent(CategoryActivity.this, EditCategoryReviewActivity.class);
                }
                intent.putExtra(CATEGORY_REVIEW, categoryReview);
                startActivity(intent);
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryReviewFragment != null) {
                    frameLayout.setVisibility(View.VISIBLE);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.show(categoryReviewFragment);
                    fragmentTransaction.commit();
                    frameLayout.setY(fragmentBottom);
                }
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
            Intent intent = new Intent(CategoryActivity.this, EditCategoryReviewActivity.class);
            if (categoryReview != null) {
                intent.putExtra("parent_id", categoryReview.getId());
            }
            startActivity(intent);
        } else if (selected.equals(getString(R.string.category_edit))) {
            Intent intent = new Intent(CategoryActivity.this, EditCategoryReviewActivity.class);
            intent.putExtra("category_review", categoryReview);
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
                "Your reviews/sub-categoryReviews for this category will be lost.");
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

    void adjustCategoryReviewFragmentView(MotionEvent event) {
        int action = event.getActionMasked();
        final int Y = (int) event.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                fragmentYDelta = (int)frameLayout.getY() - Y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(Y - fragmentYDelta) > 10) {
                    frameLayout.setY(Y + fragmentYDelta);
                    categoryReviewListView.setY(frameLayout.getY() + frameLayout.getHeight());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (getFragmentBottom() < originalFragmentY / ((float)5/2)) {
                    if (categoryReviewFragment != null) {
                        frameLayout.setVisibility(View.GONE);
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.hide(categoryReviewFragment);
                        fragmentTransaction.commit();

                        // Blame Android being retarded for this
                        final Handler handler = new Handler();
                        final Runnable runnable = new Runnable() {
                            public void run() {
                                categoryReviewListView.setY(toolbar.getY() + toolbar.getHeight());
                            }
                        };
                        handler.postDelayed(runnable, 100);

                    }
                } else if (getFragmentBottom()  > originalFragmentY) {
                    frameLayout.setY(fragmentBottom);
                    categoryReviewListView.setY(frameLayout.getY() + frameLayout.getHeight());
                } else {
                    categoryReviewListView.setY(frameLayout.getY() + frameLayout.getHeight());
                }
        }
    }

    private int getFragmentBottom() {
        return (int)frameLayout.getY() + frameLayout.getHeight();
    }

    void setFragmentBoundaries(int top) {
        categoryReviewView = findViewById(R.id.category_review_fragment);
        originalFragmentY = top + categoryReviewView.getHeight();
        fragmentBottom = top;
    }

    @Override
    public void onResume(){
        super.onResume();
        loadFragment();
        populate();
    }


    @Override
    public void finishActivity(){
        finish();
    }

    private static class AsyncTaskInsert extends AsyncTask<CategoryReview, Void, Boolean> {
        private WeakReference<CategoryActivity> categoryActivityWeakReference;

        private AsyncTaskInsert(CategoryActivity categoryActivity) {
            this.categoryActivityWeakReference = new WeakReference<>(categoryActivity);
        }

        @Override
        protected Boolean doInBackground(CategoryReview... categoryReview) {
            try {
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
