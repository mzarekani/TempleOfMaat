package templeofmaat.judgment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ReviewLayout extends FrameLayout {
    CategoryActivity activity;
    Integer top = 0;
    public ReviewLayout(@NonNull Context context) {
        super(context);
        if(context instanceof CategoryActivity)
        {
             activity = (CategoryActivity)context;
            // Then call the method in the activity.
        }
    }

    public ReviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(context instanceof CategoryActivity)
        {
            activity = (CategoryActivity)context;
            // Then call the method in the activity.
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
        Toast.makeText(activity, "hiiiii889", Toast.LENGTH_SHORT).show();
        activity.moveReview(event);
        return true;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        activity.moveReview(event);
        return true;
    }
}
