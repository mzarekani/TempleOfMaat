package templeofmaat.judgment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import templeofmaat.judgment.data.CategoryReview;


public class CategoryReviewFragment extends Fragment {
    private static final String CATEGORY_REVIEW = "category_review";

    private TextInputEditText titleView;


    private CategoryReview categoryReview;

    private OnFragmentInteractionListener mListener;

    private Context context;

    public static CategoryReviewFragment newInstance(CategoryReview categoryReview) {
        CategoryReviewFragment fragment = new CategoryReviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(CATEGORY_REVIEW, categoryReview);
        fragment.setArguments(args);
        return fragment;
    }

    public CategoryReviewFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
            categoryReview = (CategoryReview) getArguments().getSerializable(CATEGORY_REVIEW);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        titleView = view.findViewById(R.id.title);
        if (categoryReview != null) {
            titleView.setText(categoryReview.getTitle());
        }
        titleView.setInputType(InputType.TYPE_NULL);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
