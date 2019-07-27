package templeofmaat.judgment.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ReviewDao {

    @Query("SELECT * FROM " + Review.TABLE_NAME + " WHERE " + Review.COLUMN_NAME + " = :name")
    LiveData<Review> getReview(String name);

    @Query("SELECT * FROM review WHERE review.id = :id")
    LiveData<Review> getReview(int id);

    @Query("SELECT " + Review.COLUMN_NAME + " FROM " + Review.TABLE_NAME + " WHERE " + Review.COLUMN_CATEGORY_ID + " = :categoryId")
    LiveData<List<String>> getAllNamesForCategory(int categoryId);

    @Query("SELECT review.id AS " + ReviewEssentials.REVIEW_ID + ", review.name AS " + ReviewEssentials.REVIEW_NAME +
            ", category.name AS " + ReviewEssentials.CATEGORY_NAME + " FROM review JOIN category ON " +
            "review.categoryId = category.id WHERE review.categoryId = :categoryId")
    LiveData<List<ReviewEssentials>> getReviewEssentialsForCategory(int categoryId);

    @Query("SELECT review.id AS " + ReviewEssentials.REVIEW_ID + ", review.name AS " + ReviewEssentials.REVIEW_NAME +
            ", category.name AS " + ReviewEssentials.CATEGORY_NAME + " FROM review JOIN category ON " +
            "review.categoryId = category.id WHERE review.name LIKE '%' || :name || '%'")
    LiveData<List<ReviewEssentials>> getReviewEssentials(String name);

    @Insert
    void insert(Review review);

    @Update
    void update(Review view);

    @Delete
    void delete(Review review);

}
