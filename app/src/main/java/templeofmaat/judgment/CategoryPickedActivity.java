package templeofmaat.judgment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import templeofmaat.judgment.data.Category;

public class CategoryPickedActivity extends AppCompatActivity {

    private ArrayAdapter categoryListAdapter;
    private ListView categoryList;
    private CategoryPickedService categoryPickedService;
    private String categoryName;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_picked);

        categoryPickedService = new CategoryPickedService(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        categoryName = extras.containsKey("Category") ? extras.getString("Category") : "Category";
        setTitle(categoryName);


        loadAndPopulate(categoryName);
    }

    private void loadAndPopulate(String categoryName) {
        loadCategory(categoryName);
    }

    private void loadCategory(String categoryName) {
        final LiveData<Category> liveDataCategory = categoryPickedService.getCategory(categoryName);
        liveDataCategory.observe(this, new Observer<Category>() {
            @Override
            public void onChanged(@Nullable Category _category) {
                if (_category != null) {
                    category = _category;
                    loadReviews(category.getId());
                }
            }
        });
    }

    private void loadReviews(int categoryId) {
        final LiveData<List<String>> reviews = categoryPickedService.getAllNamesForCategory(categoryId);
        reviews.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> loadedReviews) {
                List<String> reviews = new ArrayList<>();
                reviews.add(getString(R.string.category_new));
                if (loadedReviews != null) {
                    reviews.addAll(loadedReviews);
                }
                reviews.add(getString(R.string.category_delete));
                populate(reviews);
            }
        });

    }

    private void populate(List<String> reviews) {
        categoryList = findViewById(R.id.reviewList);
        categoryListAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.mytextview, R.id.textview_1, reviews);
        categoryList.setAdapter(categoryListAdapter);
        addOnItemClickListener();
    }

    public void addOnItemClickListener() {
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                String reviewName = (String) categoryList.getItemAtPosition(position);
                int reviewCount = categoryList.getAdapter().getCount();
                // If User  Picked New
                if (reviewName.equals(getString(R.string.category_new)) && position == 0) {
                    Intent intent = new Intent(CategoryPickedActivity.this, EditReviewActivity.class);
                    intent.putExtra("Category", categoryName);
                    intent.putExtra("category", category);
                    intent.putExtra("reviewName", reviewName);
                    startActivity(intent);
                } else if (reviewName.equals(getString(R.string.category_delete)) && position == reviewCount - 1) {
                    deleteCategory();
                } else {
                    Intent intent = new Intent(CategoryPickedActivity.this, EditReviewActivity.class);
                    intent.putExtra("category", category);
                    intent.putExtra("reviewName", reviewName);
                    startActivity(intent);
                }
            }
        });
    }

    public void deleteCategory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,  R.style.AlertDialog);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to delete " + categoryName + "? Your reviews for this category will be lost.");
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryPickedService.deleteCategory(category);
                finish();
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

    @Override
    public void onResume(){
        super.onResume();
        if (category != null) {
            loadReviews(category.getId());
        }
    }
}
