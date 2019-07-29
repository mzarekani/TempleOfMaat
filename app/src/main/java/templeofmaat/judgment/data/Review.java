package templeofmaat.judgment.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = Review.TABLE_NAME, indices = {@Index(value = Category.COLUMN_NAME)},
        foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id",
                childColumns = "categoryId", onDelete = CASCADE))
public class Review {

    static final String TABLE_NAME = "review";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_RATING = "rating";
    static final String COLUMN_COMMENT = "comment";
    static final String COLUMN_CATEGORY_ID = "categoryId";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = Review.COLUMN_NAME)
    private String name;

    @ColumnInfo(name = Review.COLUMN_RATING)
    private float rating;

    @ColumnInfo(name = Review.COLUMN_COMMENT)
    private String comment;

    @ColumnInfo(name = Review.COLUMN_CATEGORY_ID)
    private int categoryId;

    public Review() {}

    public Review(String name, Float rating, String comment, int categoryId) {
        this.name = name;
        this.rating = rating;
        this.comment = comment;
        this.categoryId = categoryId;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
