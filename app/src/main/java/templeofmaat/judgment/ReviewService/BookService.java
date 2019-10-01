package templeofmaat.judgment.ReviewService;


import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;

import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;
import java.time.Instant;

import templeofmaat.judgment.CategoryReviewFragment;
import templeofmaat.judgment.R;
import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.Book;
import templeofmaat.judgment.data.BookDao;

public class BookService implements ReviewService {
    private static final String TAG = BookService.class.getName();

    private Book book;
    private BookDao bookDao;
    private TextInputEditText authorView;
    private RatingBar ratingBar;
    private TextInputEditText commentView;

    public void setUpService(CategoryReviewFragment categoryReviewFragment) {
        bookDao = AppDatabase.getAppDatabase(categoryReviewFragment.getContext()).bookDao();
    }

    public void loadView(View view) {
        authorView = view.findViewById(R.id.author);
        ratingBar = view.findViewById(R.id.rating_bar);
        commentView = view.findViewById(R.id.review);
    }

    public void loadValues() {
        if (book != null) {
            authorView.setText(book.getAuthor());
            ratingBar.setRating(book.getRating());
            commentView.setText(book.getComment());
        }
    }

    public void loadEntity(int categoryReviewId) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                book = bookDao.get(categoryReviewId);
            }
        });
    }

    public Instant getUpdateTime() {
        return book.getUpdateTime();
    }

    public void createReview(int categoryId) {
        if (book == null) {
            book = new Book(ratingBar.getRating(), commentView.getText().toString().trim(), authorView.getText().toString().trim());
            book.setCategoryReviewId(categoryId);
        } else {
            book.setRating(ratingBar.getRating());
            book.setComment(commentView.getText().toString().trim());
            book.setAuthor(authorView.getText().toString().trim());
        }

        new AsyncTaskInsert(BookService.this).
                execute(book);
    }

    private static class AsyncTaskInsert extends AsyncTask<Object, Void, Boolean> {
        private WeakReference<BookService> bookServiceWeakReference;
        BookService bookService;

        private AsyncTaskInsert(BookService bookService) {
            this.bookServiceWeakReference = new WeakReference<>(bookService);
            this.bookService = bookService;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            Book book = (Book) objects[0];
            try {
                bookServiceWeakReference.get().bookDao.insert(book);
               // bookService.bookDao.insert(book);
            } catch (SQLiteException exception) {
                Log.e(TAG, "Error Creating/Updating Category", exception);
                return false;
            }

            Log.i(TAG, "Created/Updated Book: " + book.getId());
            return true;
        }
    }
}
