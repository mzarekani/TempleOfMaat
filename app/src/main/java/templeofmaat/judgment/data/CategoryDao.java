package templeofmaat.judgment.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface CategoryDao {

    @Query("SELECT * FROM " + Category.TABLE_CATEGORY + " WHERE id = :id")
    LiveData<Category> getCategory(int id);

    @Query("SELECT * FROM " + Category.TABLE_CATEGORY + " WHERE " + Category.COLUMN_NAME + " = :name")
    LiveData<Category> getCategory(String name);

    @Query("SELECT * FROM " + Category.TABLE_CATEGORY)
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT name FROM category")
    LiveData<List<String>> getAllLabels();

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

}
