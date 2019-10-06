package templeofmaat.judgment.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CategoryReview categoryReview);

    @Update
    int update(CategoryReview categoryReview);

    @Delete
    void delete(CategoryReview categoryReview);

    @Query("SELECT * FROM category_review where id = :id")
    LiveData<CategoryReview> getCategoryReview(int id);

    @Query("SELECT * FROM category_review where parent_id = :parentId")
    LiveData<List<CategoryReview>> getCategoryReviewsForParent(int parentId);

    @Query("SELECT * FROM category_review where title like :title")
    LiveData<List<CategoryReview>> getCategoryReviewsForName(String title);
}
