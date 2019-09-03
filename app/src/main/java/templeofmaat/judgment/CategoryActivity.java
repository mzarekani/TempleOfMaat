package templeofmaat.judgment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;


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

    private ArrayAdapter categoryReviewAdapter;
    private ListView categoryReviewView;
    ArrayList<CategoryReview> categoryReviews;
    private CategoryReviewDao categoryReviewDao;
    CategoryReview categoryReview;
    FrameLayout frameLayout;
    float y;
    CategoryReviewFragment categoryReviewFragment;
    FragmentTransaction fragmentTransaction;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null && extras.containsKey("CategoryReview")) {
            categoryReview = (CategoryReview) extras.getSerializable("CategoryReview");
            setTitle(categoryReview.getTitle());
        } else {
            setTitle("Categories");
        }


        // Revisit when allow for multiple accounts
//        SharedPreferences sharedPref = this.getSharedPreferences("user_info", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("user_account", accountName);
//        editor.apply();
//
//        accountName = sharedPref.getString("user_account", "none");
        categoryReviewDao = AppDatabase.getAppDatabase(this).categoryReviewDao();
        categoryReviewView = findViewById(R.id.categoryList);
        if (getDatabasePath(AppDatabase.DATABASE_NAME).exists()){
            populate();
        } else {
            Log.i(TAG, "Creating database for new user");
            initialize();
        }

        addListeners();

        if (categoryReview != null && categoryReview.isReview() && savedInstanceState == null) {
            categoryReviewFragment = CategoryReviewFragment.newInstance(categoryReview);
            categoryReviewFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.article_fragment, categoryReviewFragment).commit();
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
                    categoryReviewView.setAdapter(categoryReviewAdapter);
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
        categoryReviewView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Intent intent;
                CategoryReview categoryReview = (CategoryReview) categoryReviewView.getItemAtPosition(position);
                if (categoryReview.isCategory()) {
                    intent = new Intent(CategoryActivity.this, CategoryActivity.class);
                } else {
                    intent = new Intent(CategoryActivity.this, EditCategoryReviewActivity.class);
                }
                intent.putExtra("CategoryReview", categoryReview);
                startActivity(intent);
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryReviewFragment != null) {
                    frameLayout.setVisibility(View.VISIBLE);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                            android.R.anim.fade_out);
                    fragmentTransaction.show(categoryReviewFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        final Animation categoryReviewFragmentExit = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        categoryReviewFragmentExit.setDuration(500);

        frameLayout = findViewById(R.id.article_fragment);
        frameLayout.setOnTouchListener(new FrameLayout.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                int action = event.getActionMasked();
                float distance = 75;
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        y = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        // Dragged Up
                        if (distance < y - event.getY()) {
                            frameLayout.startAnimation(categoryReviewFragmentExit);
                            Toast.makeText(CategoryActivity.this, "up", Toast.LENGTH_SHORT).show();
                        }
                }
                return true;
            }
        });

        categoryReviewFragmentExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                frameLayout.setVisibility(View.GONE);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(categoryReviewFragment);
                fragmentTransaction.commit();
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
            intent.putExtra("category_id", categoryReview);
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

    @Override
    public void onResume(){
        super.onResume();
        populate();
    }


    @Override
    public void onFragmentInteraction(Uri uri){
        Toast.makeText(this, "w/e", Toast.LENGTH_LONG).show();
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
