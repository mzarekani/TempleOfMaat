package templeofmaat.judgment.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import templeofmaat.judgment.data.Review;

@Dao
public interface ReviewDao {

    @Query("SELECT name FROM review")
    LiveData<List<String>> getAllNames();

    @Query("SELECT * FROM " + Review.TABLE_NAME + " WHERE " + Review.COLUMN_NAME + " = :name")
    LiveData<Review> getReview(String name);

    @Query("SELECT " + Review.COLUMN_NAME + " FROM " + Review.TABLE_NAME + " WHERE " + Review.COLUMN_CATEGORY_ID + " = :categoryId")
    LiveData<List<String>> getAllNamesForCategory(int categoryId);

    @Insert
    void insert(Review review);

    @Update
    void update(Review view);

    @Delete
    void delete(Review review);

}
