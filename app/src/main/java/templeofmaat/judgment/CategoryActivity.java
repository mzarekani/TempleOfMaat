package templeofmaat.judgment;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import templeofmaat.judgment.data.Category;


public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = CategoryActivity.class.getName();

    private String accountName;
    private CategoryService categoryService;
    private ArrayAdapter categoryListAdapter;
    private ListView categoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setTitle("Categories");

        categoryService = new CategoryService(this);

        accountName = "lykus";
        SharedPreferences sharedPref = this.getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user_account", accountName);
        editor.apply();

        accountName = sharedPref.getString("user_account", "none");
        if (this.getDatabasePath(accountName).exists()){
            populate();
        } else {
            Log.d(TAG, "Creating database for user: " + accountName);
            initialize();
        }

        addOnItemClickListener();
    }

    public void addOnItemClickListener() {
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                String itemValue = (String) categoryList.getItemAtPosition(position);
                // If User  Picked New
                if (position == 0) {
                    createNewCategory();
                } else {
                    Intent intent = new Intent(CategoryActivity.this, CategoryPickedActivity.class);
                    intent.putExtra("Category", itemValue);
                    startActivity(intent);
                }
            }
        });
    }

    public void createNewCategory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        builder.setTitle("New Category Name");

        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCategory = input.getText().toString();
                categoryService.insertCategory(new Category(newCategory));
                Log.d(TAG, "New Category Created: " + newCategory);
                populate();
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

    public void populate(){
        categoryList = findViewById(R.id.categoryList);
        final LiveData<List<String>> categories = categoryService.getAllLabels();
        categories.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> loadedCategories) {
                List<String> categories = new ArrayList<>();

                categories.add("New");
                if (loadedCategories != null) {
                    categories.addAll(loadedCategories);
                }
                categoryListAdapter = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.mytextview, R.id.textview_1, categories);
                categoryList.setAdapter(categoryListAdapter);

            }
        });
    }

    public void initialize(){
        categoryService.insertCategory(new Category("Restaurants"));
        categoryService.insertCategory(new Category("Books"));
        populate();
    }

    @Override
    public void onResume(){
        super.onResume();
        populate();
    }

}
