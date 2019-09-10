package templeofmaat.judgment.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Book book);

    @Update
    void update(Book book);

    @Query("SELECT * FROM book WHERE category_review_id = :categoryReviewId")
    Book getBook(int categoryReviewId);
}
