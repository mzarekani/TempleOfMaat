package templeofmaat.judgment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import templeofmaat.judgment.data.CategoryReview;

public class EditCategoryReviewActivity extends AppCompatActivity implements CategoryReviewFragment.OnFragmentInteractionListener {

    private static final String TAG = EditCategoryReviewActivity.class.getName();

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

    @Override
    public void finishActivity(){
        finish();
    }
}
