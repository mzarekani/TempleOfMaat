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

public class SearchActivity extends AppCompatActivity {
    private AppDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = AppDatabase.getAppDatabase(this);

      //  loadReviewEssentials(getIntent());
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        loadReviewEssentials(intent);
//    }

//    private void loadReviewEssentials(Intent intent) {
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            LiveData<List<ReviewEssentials>> reviewEssentials = db.reviewDao().getReviewEssentials(query);
//            reviewEssentials.observe(this, new Observer<List<ReviewEssentials>>() {
//                @Override
//                public void onChanged(List<ReviewEssentials> reviews) {
//                    if (reviews != null) {
//                        populate(reviews);
//                    }
//                }
//            });
//        }
//    }
//
//    private void populate(final List<ReviewEssentials> reviews) {
//        ListView searchResults = findViewById(R.id.searchResults);
//        ArrayAdapter searchResultsAdapter = new ArrayAdapter<>(this,
//                R.layout.mytextview, R.id.textview_1, reviews);
//        searchResults.setAdapter(searchResultsAdapter);
//
//        addOnItemClickListener(searchResults);
//    }
//
//    public void addOnItemClickListener(final ListView searchResults) {
//        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1,
//                                    int position, long arg3) {
//                ReviewEssentials review = (ReviewEssentials) searchResults.getItemAtPosition(position);
//                    Intent intent = new Intent(SearchActivity.this, EditReviewActivity.class);
//                    intent.putExtra("review", review);
//                    startActivity(intent);
//            }
//        });
//    }

}
