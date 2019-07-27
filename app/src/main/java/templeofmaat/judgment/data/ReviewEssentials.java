package templeofmaat.judgment.data;

import androidx.room.ColumnInfo;

import java.io.Serializable;

public class ReviewEssentials implements Serializable {
    static final String REVIEW_ID = Review.TABLE_NAME + "_id";
    static final String REVIEW_NAME = Review.TABLE_NAME + "_" + Review.COLUMN_NAME;
    static final String CATEGORY_NAME = Category.TABLE_NAME + "_" + Category.COLUMN_NAME;

    @ColumnInfo(name = REVIEW_ID)
    private int id;
    @ColumnInfo(name = REVIEW_NAME)
    private String name;
    @ColumnInfo(name = CATEGORY_NAME)
    private String category;

    public ReviewEssentials(String name, String category) {
        this.name = name;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return name;
    }
}
