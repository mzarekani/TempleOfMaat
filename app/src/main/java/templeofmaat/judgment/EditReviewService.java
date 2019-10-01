//package templeofmaat.judgment;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//import androidx.lifecycle.LiveData;
//
//import templeofmaat.judgment.data.AppDatabase;
//
//class EditReviewService {
//    private AppDatabase db;
//
//    EditReviewService(Context context) {
//        db = AppDatabase.getAppDatabase(context);
//    }
//
//    LiveData<ReviewService> getReview(final String name) {
//        return db.reviewDao().getReview(name);
//    }
//
//    void saveReview(final ReviewService review) {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                db.reviewDao().insert(review);
//            }
//        });
//    }
//
//    void updateReview(final ReviewService review) {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                db.reviewDao().update(review);
//            }
//        });
//    }
//
//    void deleteReview(final ReviewService review) {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                db.reviewDao().delete(review);
//            }
//        });
//    }
//}
