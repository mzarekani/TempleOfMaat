package templeofmaat.judgment;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.ref.WeakReference;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import templeofmaat.judgment.data.AppDatabase;
import templeofmaat.judgment.data.Book;
import templeofmaat.judgment.data.BookDao;
import templeofmaat.judgment.data.CategoryReview;
import templeofmaat.judgment.data.CategoryReviewDao;


public class CategoryReviewFragment extends Fragment {
    private static final String TAG = CategoryReviewFragment.class.getName();
    private static final String CATEGORY_REVIEW = "category_review";
    private static final String PARENT_ID = "parent_id";
    private static final String EDITABLE = "editable";

    private View view;
    private TextInputEditText titleView;
    private TextInputLayout authorLayout;
    private TextInputEditText authorView;
    private RadioGroup categoryReviewType;
    private Spinner reviewTypeSpinner;
    private RatingBar ratingBar;
    private TextInputLayout commentLayout;
    private TextInputEditText commentView;
    private TextView dateView;

    private Book book;
    private CategoryReview categoryReview;
    private Integer parentId;
    private boolean editable;

    private List<ReviewType> reviewTypes;

    private CategoryReviewDao categoryReviewDao;
    private BookDao bookDao;

    private OnFragmentInteractionListener fragmentInteractionListener;
    private Context context;

    static CategoryReviewFragment newInstance(CategoryReview categoryReview, Integer parentId, Boolean editable) {
        CategoryReviewFragment categoryReviewFragment = new CategoryReviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(CATEGORY_REVIEW, categoryReview);
        args.putSerializable(PARENT_ID, parentId);
        args.putBoolean(EDITABLE, editable);
        categoryReviewFragment.setArguments(args);
        return categoryReviewFragment;
    }

    public CategoryReviewFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            fragmentInteractionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            editable = getArguments().getBoolean("editable");
            categoryReview = (CategoryReview) getArguments().getSerializable(CATEGORY_REVIEW);
            parentId = (Integer) getArguments().getSerializable("parent_id");
            categoryReviewDao = AppDatabase.getAppDatabase(context).categoryReviewDao();
            bookDao = AppDatabase.getAppDatabase(context).bookDao();
            if (categoryReview != null && categoryReview.isReview() && categoryReview.getReviewType().equals(ReviewType.Book.toString())) {
                loadBook();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        this.view = view;

        if (!editable) {
            disableView(view);
        }

        setTitleView();
        setDateView();
        setAuthorView();
        setRatingBarView();
        setCommentView();
        setReviewTypeView();
        setCategoryReviewTypeView();
    }

    private void disableView(View view) {
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(false);
            if (child instanceof ViewGroup && ((ViewGroup) child).getChildCount() != 0) {
                disableView(child);
            }
        }
    }

    private void setTitleView() {
        titleView = view.findViewById(R.id.title);
        if (categoryReview != null) {
            titleView.setText(categoryReview.getTitle());
        }
    }

    private void setDateView() {
        dateView = view.findViewById(R.id.date);
        DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        .withLocale(Locale.US)
                        .withZone(ZoneId.systemDefault());
        String time;
        if (book != null && book.getUpdateTime().isAfter(categoryReview.getUpdateTime())) {
            time = formatter.format(book.getUpdateTime());
        } else {
            time = formatter.format(categoryReview.getUpdateTime());
        }
        dateView.setText(time);
    }

    private void setAuthorView() {
        authorLayout = view.findViewById(R.id.text_input_layout_author);
        authorView = view.findViewById(R.id.author);
        if (book != null && book.getAuthor() != null && !book.getAuthor().isEmpty()) {
            authorView.setText(book.getAuthor());
        } else if (!editable) {
            authorLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void setRatingBarView() {
        ratingBar = view.findViewById(R.id.rating_bar);
        if (book != null && book.getRating() != null) {
            ratingBar.setRating(book.getRating());
        } else if (!editable){
            ratingBar.setVisibility(View.INVISIBLE);
        }
    }

    private void setCommentView() {
        commentLayout = view.findViewById(R.id.text_input_layout_comments);
        commentView = view.findViewById(R.id.review);
        if (book != null && book.getComment() != null ) {
            commentView.setText(book.getComment());
        } else if (!editable){
            commentLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void setReviewTypeView() {
        reviewTypes = new ArrayList<>(EnumSet.allOf(ReviewType.class));
        ArrayAdapter<ReviewType> adapter = new ArrayAdapter<ReviewType>(context,  R.layout.spinner_text_view, reviewTypes) {
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                convertView = super.getDropDownView(position, convertView, parent);
                convertView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams p = convertView.getLayoutParams();
                p.height = 100;
                convertView.setLayoutParams(p);

                return convertView;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reviewTypeSpinner = view.findViewById(R.id.selectTypeSpinner);
        reviewTypeSpinner.setAdapter(adapter);
        reviewTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                ReviewType selected =  (ReviewType) adapterView.getSelectedItem();
                if (selected == ReviewType.Book) {
                    view.findViewById(R.id.review_book).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.review_book).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (categoryReview != null) {
            reviewTypeSpinner.setSelection(reviewTypes.indexOf(ReviewType.valueOf(categoryReview.getReviewType())));
        } else {
            reviewTypeSpinner.setSelection(reviewTypes.indexOf(ReviewType.SELECT));
        }
    }

    private void setCategoryReviewTypeView() {
        categoryReviewType = view.findViewById(R.id.radio_group_category_review_type);
        categoryReviewType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton checkedRadioButton = radioGroup.findViewById(checkedId);
                boolean checked = checkedRadioButton.isChecked();

                switch(checkedId) {
                    case R.id.radio_category:
                        if (checked) {
                            reviewTypeSpinner.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case R.id.radio_review:
                        if (checked) {
                            reviewTypeSpinner.setVisibility(View.VISIBLE);
                        }
                        break;
                    case R.id.radio_category_review:
                        if (checked) {
                            reviewTypeSpinner.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        });

        if (categoryReview != null ) {
            if (categoryReview.isReview() && categoryReview.isCategory()) {
                categoryReviewType.check(R.id.radio_category_review);
            } else if (categoryReview.isCategory()) {
                categoryReviewType.check(R.id.radio_category);
            } else if (categoryReview.isReview()) {
                categoryReviewType.check(R.id.radio_review);
            }
        }

    }

    private void loadBook() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                book = bookDao.getBook(categoryReview.getId());
            }
        });
    }

    void save() {
        String title = titleView.getText().toString().trim();
        if(!validateTitle(title)) {
            return;
        }

        if (categoryReview == null) {
            categoryReview = new CategoryReview(title);
            categoryReview.setParentId(parentId);
        } else {
            categoryReview.setTitle(title);
            categoryReview.setUpdateTime(Instant.now());
        }

        ReviewType selected = (ReviewType) reviewTypeSpinner.getSelectedItem();
        RadioGroup categoryReviewType = view.findViewById(R.id.radio_group_category_review_type);
        if (categoryReviewType.getCheckedRadioButtonId() == R.id.radio_category) {
            categoryReview.setCategory(true);
            categoryReview.setReview(false);
            categoryReview.setReviewType(null);
            new CategoryReviewFragment.AsyncTaskInsert(CategoryReviewFragment.this).
                    execute(categoryReview);
        } else {
            boolean isCategoryReview = categoryReviewType.getCheckedRadioButtonId() == R.id.radio_category_review;
            if (selected == ReviewType.SELECT) {
                Toast.makeText(context,
                        "Must pick a type", Toast.LENGTH_LONG)
                        .show();
            } else if (selected == ReviewType.Book) {
                categoryReview.setCategory(isCategoryReview);
                categoryReview.setReview(true);
                categoryReview.setReviewType(selected.toString());

                String author;
                if (authorView.getText().toString().trim().isEmpty()) {
                    author = null;
                } else {
                    author = authorView.getText().toString().trim();
                }

                if (book == null) {
                    book = new Book(ratingBar.getRating(), commentView.getText().toString(), author);
                } else {
                    book.setUpdateTime(Instant.now());
                    book.setAuthor(author);
                    book.setRating(ratingBar.getRating());
                    book.setComment(commentView.getText().toString());
                }
                new CategoryReviewFragment.AsyncTaskInsert(CategoryReviewFragment.this).
                        execute(categoryReview, book);
            }
        }
    }

    private boolean validateTitle(String title) {
        boolean titleValid = false;
        if (title.isEmpty()) {
            Toast.makeText(context,
                    "Title can't be empty", Toast.LENGTH_LONG)
                    .show();
        } else {
            titleValid = true;
        }
        return titleValid;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    public interface OnFragmentInteractionListener {
        void finishActivity();
    }

    private static class AsyncTaskInsert extends AsyncTask<Object, Void, Boolean> {
        private WeakReference<CategoryReviewFragment> categoryReviewFragmentWeakReference;

        private AsyncTaskInsert(CategoryReviewFragment categoryReviewFragment) {
            this.categoryReviewFragmentWeakReference = new WeakReference<>(categoryReviewFragment);
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            CategoryReview categoryReview = (CategoryReview) objects[0];
            long id;
            try {
                id = categoryReviewFragmentWeakReference.get().categoryReviewDao.insert(categoryReview);
            } catch (SQLiteException exception) {
                Log.e(TAG, "Error Creating/Updating Category", exception);
                return false;
            }

            if (categoryReview.isReview()) {
                if (categoryReview.getReviewType().equals(ReviewType.Book.toString())) {
                    Book book = (Book) objects[1];
                    book.setCategoryReviewId((int)id);
                    categoryReviewFragmentWeakReference.get().bookDao.insert(book);
                }
            }

            Log.i(TAG, "Created/Updated category: " + categoryReview.getTitle());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                CategoryReviewFragment categoryReviewFragment = categoryReviewFragmentWeakReference.get();
                if (categoryReviewFragment != null) {
                    categoryReviewFragment.fragmentInteractionListener.finishActivity();
                }
            }
        }
    }
}
