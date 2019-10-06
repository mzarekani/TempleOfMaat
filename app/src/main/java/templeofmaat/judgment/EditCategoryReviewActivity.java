package templeofmaat.judgment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import templeofmaat.judgment.data.CategoryReview;

public class EditCategoryReviewActivity extends AppCompatActivity implements CategoryReviewFragment.OnFragmentInteractionListener {

    private static final String TAG = EditCategoryReviewActivity.class.getName();

    private CategoryReview categoryReview;
    private Integer parentId;
    CategoryReviewFragment categoryReviewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category_review);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Constants.CATEGORY_REVIEW)) {
                categoryReview = (CategoryReview) extras.getSerializable(Constants.CATEGORY_REVIEW);
                setTitle(categoryReview.getTitle());
            } else if (extras.containsKey(Constants.PARENT_ID)) {
                parentId = extras.getInt(Constants.PARENT_ID);
            }
        } else {
            setTitle("New Entry");
        }

        setUpListeners();

        if (categoryReview != null && categoryReview.isReview() && savedInstanceState == null) {
            categoryReviewFragment = CategoryReviewFragment.newInstance(categoryReview, null, true);
            getSupportFragmentManager().beginTransaction().add(R.id.category_review_fragment, categoryReviewFragment).commit();
        } else if (parentId != null && savedInstanceState == null) {
            categoryReviewFragment = CategoryReviewFragment.newInstance(null, parentId, true);
            getSupportFragmentManager().beginTransaction().add(R.id.category_review_fragment, categoryReviewFragment).commit();
        }

    }

    public void setUpListeners() {
        onSave();
        onCancel();
    }

    public void onSave() {
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                categoryReviewFragment.save();
            }
        });
    }

    public void onCancel() {
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (categoryReview != null) {
            getMenuInflater().inflate(R.menu.options_menu, menu);
            menu.add(getString(R.string.category_delete));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        CharSequence selected = item.getTitle();
        if (selected.equals(getString(R.string.category_delete))) {
            confirmDeleteCategoryReview();
        }

        return true;
    }

    private void confirmDeleteCategoryReview() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,  R.style.AlertDialog);
        builder.setTitle("Confirm");
        StringBuilder message = new StringBuilder("Are you sure you want to delete " + categoryReview.getTitle() + "? ");
        if (categoryReview.isCategory()) {
            message.append("Any existing sub-reviews or sub-categories will also be deleted.");
        }
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryReviewFragment.deleteCategoryReview();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void finishActivity(){
        finish();
    }
}
