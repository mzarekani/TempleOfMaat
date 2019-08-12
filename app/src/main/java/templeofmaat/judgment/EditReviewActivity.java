package templeofmaat.judgment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.Category;
import templeofmaat.judgment.data.Review;
import templeofmaat.judgment.data.ReviewEssentials;

public class EditReviewActivity extends AppCompatActivity {

    private static final String TAG = EditReviewActivity.class.getName();

    private Review review;
    private Category category;
    private EditReviewService editReviewService;
    private ReviewEssentials reviewEssentials;
    private AppDatabase db;
    private TextInputEditText commentView;
    private TextView nameView;
    private RatingBar starRatingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        nameView = findViewById(R.id._name);
        commentView = findViewById(R.id.review);
        starRatingView = findViewById(R.id.stars);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        reviewEssentials = (ReviewEssentials) extras.getSerializable("review");
        setTitle(reviewEssentials.getCategory());

        editReviewService = new EditReviewService(this);

        db = AppDatabase.getAppDatabase(this);
        loadCategory();
        if (!reviewEssentials.getName().equals(getString(R.string.review_new))){
            loadReview();
        }

        setUpListeners();
    }

    private void loadCategory() {
        final LiveData<Category> liveCategory = db.categoryDao().getCategory(reviewEssentials.getCategory());
        liveCategory.observe(this, new Observer<Category>() {
            @Override
            public void onChanged(@Nullable Category loadedCategory) {
                if (loadedCategory != null) {
                    category = loadedCategory;
                }
                liveCategory.removeObserver(this);
            }
        });
    }

    private void loadReview() {
        final LiveData<Review> liveReview = db.reviewDao().getReview(reviewEssentials.getId());
        liveReview.observe(this, new Observer<Review>() {
            @Override
            public void onChanged(@Nullable Review loadedReview) {
                if (loadedReview != null) {
                    review = loadedReview;
                    review.setId(loadedReview.getId());
                    nameView.append(loadedReview.getName());
                    starRatingView.setRating(loadedReview.getRating());
                    commentView.append(loadedReview.getComment());
                }
                liveReview.removeObserver(this);
            }
        });
    }

    private void setUpListeners() {
        Button saveButton = findViewById(R.id.saveButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = nameView.getText().toString().trim();
                String comment = commentView.getText().toString().trim();
                if (validateName(name)) {
                    if (reviewEssentials.getName().equals(getString(R.string.review_new))) {
                        review = new Review(name, starRatingView.getRating(), comment, category.getId());
                        editReviewService.saveReview(review);
                        Log.i(TAG, "Created New Review " + name + " for category " + category.getName());
                    } else {
                        review.setName(name);
                        review.setRating(starRatingView.getRating());
                        review.setComment(comment);
                        editReviewService.updateReview(review);
                        Log.i(TAG, "Updated Review " + name + " for category " + category.getName());
                    }
                    finish();
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editReviewService.deleteReview(review);
                Log.i(TAG, "Deleted Review " + review.getName() + " for category " + category.getName());
                finish();
            }
        });
    }

    private boolean validateName(String name) {
        boolean newNameValid = false;
        if (name.isEmpty()) {
            Toast.makeText(this,
                    "Name can't be empty", Toast.LENGTH_LONG)
                    .show();
        } else {
            newNameValid = true;
        }

        return newNameValid;
    }
}
