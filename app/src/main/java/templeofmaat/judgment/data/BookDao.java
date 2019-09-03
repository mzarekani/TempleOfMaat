package templeofmaat.judgment.data;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface BookDao {
    @Insert
    void insert(Book book);
}
