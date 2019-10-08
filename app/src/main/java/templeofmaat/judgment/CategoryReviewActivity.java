package templeofmaat.judgment;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;


import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.CategoryReview;
import templeofmaat.judgment.data.CategoryReviewDao;


public class CategoryReviewActivity extends AppCompatActivity implements CategoryReviewFragment.OnFragmentInteractionListener {
    private static final String TAG = CategoryReviewActivity.class.getName();

    private ArrayAdapter categoryReviewAdapter;
    private ListView categoryReviewListView;
    private ArrayList<CategoryReview> categoryReviews;
    private CategoryReviewDao categoryReviewDao;
    private CategoryReview categoryReview;
    private FrameLayout frameLayout;
    private float fragmentBottom;
    private CategoryReviewFragment categoryReviewFragment;
    private FragmentTransaction fragmentTransaction;
    private Toolbar toolbar;
    private int fragmentYDelta;
    private Integer originalFragmentY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_review);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.category_review_fragment);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        categoryReview = (CategoryReview) extras.getSerializable(Constants.CATEGORY_REVIEW);
        setTitle(categoryReview.getTitle());

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
        populate();

        addListeners();

    }

    private void loadFragment() {
        if (categoryReview != null && categoryReview.isReview()) {
            categoryReviewFragment = CategoryReviewFragment.newInstance(categoryReview, null,false);
            getSupportFragmentManager().beginTransaction().add(R.id.category_review_fragment, categoryReviewFragment).commit();
        }
    }

    public void populate(){
        LiveData<List<CategoryReview>> liveCategoryReviews = categoryReviewDao.getCategoryReviews(categoryReview.getId());
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

    @SuppressLint("ClickableViewAccessibility")
    public void addListeners() {
        categoryReviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Intent intent;
                CategoryReview categoryReview = (CategoryReview) categoryReviewListView.getItemAtPosition(position);
                if (categoryReview.isCategory()) {
                    intent = new Intent(CategoryReviewActivity.this, CategoryReviewActivity.class);
                } else {
                    intent = new Intent(CategoryReviewActivity.this, EditCategoryReviewActivity.class);
                }
                intent.putExtra(Constants.CATEGORY_REVIEW, categoryReview);
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

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getString(R.string.search_hint) + " " +  categoryReview.getTitle());
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent searchIntent = new Intent(CategoryReviewActivity.this, SearchActivity.class);
                searchIntent.putExtra(SearchManager.QUERY, query);
                searchIntent.putExtra(Constants.PARENT_ID, categoryReview.getId());
                searchIntent.setAction(Intent.ACTION_SEARCH);
                startActivity(searchIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        CharSequence selected = item.getTitle();
        if (selected.equals(getString(R.string.category_new))) {
            Intent intent = new Intent(CategoryReviewActivity.this, EditCategoryReviewActivity.class);
            if (categoryReview != null) {
                intent.putExtra(Constants.PARENT_ID, categoryReview.getId());
            }
            startActivity(intent);
        } else if (selected.equals(getString(R.string.category_edit))) {
            Intent intent = new Intent(CategoryReviewActivity.this, EditCategoryReviewActivity.class);
            intent.putExtra(Constants.CATEGORY_REVIEW, categoryReview);
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
        originalFragmentY = top + findViewById(R.id.category_review_fragment).getHeight();
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

}
