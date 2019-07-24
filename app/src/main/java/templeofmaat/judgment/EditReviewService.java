package templeofmaat.judgment;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

public class EditReviewService {
    private Context context;
    private AppDatabase db;

    EditReviewService(Context context) {
        this.context = context;

        db = Room.databaseBuilder(context,
                AppDatabase.class, "lykus").build();
    }

    LiveData<Review> getReview(final String name) {
        return db.reviewDao().getReview(name);
    }

    void saveReview(final Review review) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db.reviewDao().insert(review);
            }
        });
    }

    void updateReview(final Review review) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db.reviewDao().update(review);
            }
        });
    }

    void deleteReview(final Review review) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db.reviewDao().delete(review);
            }
        });
    }
}
