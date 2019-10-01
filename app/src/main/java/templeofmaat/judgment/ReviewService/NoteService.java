package templeofmaat.judgment.ReviewService;

import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;
import java.time.Instant;

import templeofmaat.judgment.CategoryReviewFragment;
import templeofmaat.judgment.R;
import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.Note;
import templeofmaat.judgment.data.NoteDao;

public class NoteService implements ReviewService {
    private static final String TAG = NoteService.class.getName();

    private Note note;
    private NoteDao noteDao;
    private TextInputEditText commentView;


    public void setUpService(CategoryReviewFragment categoryReviewFragment) {
        noteDao = AppDatabase.getAppDatabase(categoryReviewFragment.getContext()).noteDao();
    }

    public void loadView(View view) {
        commentView = view.findViewById(R.id.review);
    }

    public void loadValues() {
        if (note != null) {
            commentView.setText(note.getComment());
        }
    }

    public void loadEntity(int categoryReviewId) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                note = noteDao.get(categoryReviewId);
            }
        });
    }

    public Instant getUpdateTime() {
        return note.getUpdateTime();
    }

    public void createReview(int categoryReviewId) {
        if (note == null) {
            note = new Note(commentView.getText().toString().trim());
            note.setCategoryReviewId(categoryReviewId);
        } else {
            note.setComment(commentView.getText().toString().trim());
            note.setUpdateTime(Instant.now());
        }

        new NoteService.AsyncTaskInsert(NoteService.this).
                execute(note);
    }

    private static class AsyncTaskInsert extends AsyncTask<Object, Void, Boolean> {
        private WeakReference<NoteService> noteServiceWeakReference;

        private AsyncTaskInsert(NoteService noteService) {
            this.noteServiceWeakReference = new WeakReference<>(noteService);
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            Note note = (Note) objects[0];
            try {
                noteServiceWeakReference.get().noteDao.insert(note);
            } catch (SQLiteException exception) {
                Log.e(TAG, "Error Creating/Updating Category", exception);
                return false;
            }

            Log.i(TAG, "Created/Updated Note: " + note.getId());
            return true;
        }
    }
}
