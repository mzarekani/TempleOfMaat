package templeofmaat.judgment.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = Category.TABLE_CATEGORY)
public class Category implements Serializable {

    static final String TABLE_CATEGORY = "category";
    static final String COLUMN_NAME = "name";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = Category.COLUMN_NAME)
    private String name;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
