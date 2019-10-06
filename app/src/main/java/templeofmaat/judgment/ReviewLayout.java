package templeofmaat.judgment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class ReviewLayout extends FrameLayout {
    CategoryReviewActivity activity;
    Integer top = 0;
    public ReviewLayout(@NonNull Context context) {
        super(context);
        if(context instanceof CategoryReviewActivity) {
             activity = (CategoryReviewActivity)context;
        }
    }

    public ReviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(context instanceof CategoryReviewActivity) {
            activity = (CategoryReviewActivity)context;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        top = t;
        activity.setFragmentBoundaries(top);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        activity.adjustCategoryReviewFragmentView(event);
        return true;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        activity.adjustCategoryReviewFragmentView(event);
        return true;
    }
}
