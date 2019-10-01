package templeofmaat.judgment.ReviewService;


import android.view.View;

import java.time.Instant;

import templeofmaat.judgment.CategoryReviewFragment;

public interface ReviewService {
    void setUpService(CategoryReviewFragment categoryReviewFragment);
    void loadView(View view);
    void loadValues();
    void loadEntity(int categoryReviewId);
    Instant getUpdateTime();
    void createReview(int categoryReviewId);
}
