package templeofmaat.judgment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.CategoryReview;
import templeofmaat.judgment.data.CategoryReviewDao;


public class SearchActivity extends AppCompatActivity {
    private CategoryReviewDao categoryReviewDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        categoryReviewDao = AppDatabase.getAppDatabase(this).categoryReviewDao();

        loadReviewEssentials(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadReviewEssentials(intent);
    }

    private void loadReviewEssentials(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String title = "%" + intent.getStringExtra(SearchManager.QUERY) + "%";
            LiveData<List<CategoryReview>> liveCategoryReviews;

            if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.PARENT_ID)) {
                liveCategoryReviews = categoryReviewDao.getCategoryReviews(intent.getExtras()
                        .getInt(Constants.PARENT_ID), title);
            } else {
                liveCategoryReviews = categoryReviewDao.getCategoryReviews(title);
            }

            liveCategoryReviews.observe(this, new Observer<List<CategoryReview>>() {
                @Override
                public void onChanged(List<CategoryReview> categoryReviews) {
                    if (categoryReviews != null) {
                        populate(categoryReviews);
                    }
                }
            });
        }
    }

    private void populate(final List<CategoryReview> categoryReviews) {
        ListView searchResults = findViewById(R.id.searchResults);
        ArrayAdapter searchResultsAdapter = new ArrayAdapter<>(this,
                R.layout.mytextview, R.id.textview_1, categoryReviews);
        searchResults.setAdapter(searchResultsAdapter);

        addOnItemClickListener(searchResults);
    }

    public void addOnItemClickListener(final ListView searchResults) {
        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Intent intent;
                CategoryReview categoryReview = (CategoryReview) searchResults.getItemAtPosition(position);
                if (categoryReview.isCategory()) {
                    intent = new Intent(SearchActivity.this, CategoryReviewActivity.class);
                } else {
                    intent = new Intent(SearchActivity.this, EditCategoryReviewActivity.class);
                }
                intent.putExtra(Constants.CATEGORY_REVIEW, categoryReview);
                startActivity(intent);
            }
        });
    }

}
