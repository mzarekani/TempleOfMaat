package templeofmaat.judgment.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryReviewDao {

    @Insert
    long insert(CategoryReview categoryReview);

    @Update
    void update(CategoryReview categoryReview);

    @Delete
    void delete(CategoryReview categoryReview);

    @Query("SELECT * FROM category_review where parent_id is null")
    LiveData<List<CategoryReview>> getRootReviewCategories();

    @Query("SELECT * FROM category_review where parent_id = :parentId")
    LiveData<List<CategoryReview>> getReviewCategoriesForParent(int parentId);
}
