package templeofmaat.judgment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import templeofmaat.judgment.data.Category;
import templeofmaat.judgment.data.Review;

public class EditReviewActivity extends AppCompatActivity {

    private static final String TAG = EditReviewActivity.class.getName();

    //private Button saveButton, deleteButton;
    TextInputEditText commentView;
    TextView nameView;
    RatingBar starRatingView;
    private String reviewName;
    private Review review;
    EditReviewService editReviewService;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_edit_review);
        } catch (Exception e) {
            Log.e(TAG, "fuck this", e);
        }


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        category = (Category) extras.getSerializable("category");
        reviewName = extras.getString("reviewName");

        setTitle(category.getName());

        editReviewService = new EditReviewService(this);

        nameView = findViewById(R.id._name);
        commentView = findViewById(R.id.review);
        starRatingView = findViewById(R.id.stars);

        if (!reviewName.equals(getString(R.string.review_new))){
            final LiveData<Review> liveReview = editReviewService.getReview(reviewName);
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
                    liveReview.removeObserver(this); }
            });
        }
        addListeners();
    }

    public void addListeners() {
        Button saveButton = findViewById(R.id.saveButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (reviewName.equals(getString(R.string.review_new))) {
                    review = new Review(nameView.getText().toString(), starRatingView.getRating(), commentView.getText().toString(), category.getId());
                    editReviewService.saveReview(review);
                    Log.i(TAG, "Created New Review " + review.getName() + " for category " + category.getName());
                } else {
                    review.setName(nameView.getText().toString());
                    review.setRating(starRatingView.getRating());
                    review.setComment(commentView.getText().toString());
                    editReviewService.updateReview(review);
                    Log.i(TAG, "Updated Review " + review.getName() + " for category " + category.getName());
                }
                finish();
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
}
