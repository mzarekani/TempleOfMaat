package templeofmaat.judgment.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.Instant;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = Book.TABLE_NAME,
        indices = {@Index(Book.COLUMN_CATEGORY_REVIEW_ID)},
        foreignKeys = @ForeignKey(entity = CategoryReview.class, parentColumns = "id",
                childColumns = Book.COLUMN_CATEGORY_REVIEW_ID, onDelete = CASCADE, onUpdate = CASCADE))
public class Book {

    static final String TABLE_NAME = "book";
    static final String COLUMN_CREATE_TIME = "create_time";
    static final String COLUMN_UPDATE_TIME = "update_time";
    static final String COLUMN_CATEGORY_REVIEW_ID = "category_review_id";
    static final String COLUMN_RATING = "rating";
    static final String COLUMN_COMMENT = "comment";
    static final String COLUMN_AUTHOR = "author";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = Book.COLUMN_CREATE_TIME)
    @NonNull
    private Instant createTime;

    @ColumnInfo(name = Book.COLUMN_UPDATE_TIME)
    @NonNull
    private Instant updateTime;

    @ColumnInfo(name = Book.COLUMN_CATEGORY_REVIEW_ID)
    private int categoryReviewId;

    @ColumnInfo(name = Book.COLUMN_RATING)
    private Float rating;

    @ColumnInfo(name = Book.COLUMN_COMMENT)
    private String comment;

    @ColumnInfo(name = Book.COLUMN_AUTHOR)
    private String author;

    public Book(Float rating, String comment, String author) {
        this.rating = rating;
        this.comment = comment;
        this.author = author;
        createTime = Instant.now();
        updateTime = Instant.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public int getCategoryReviewId() {
        return categoryReviewId;
    }

    public void setCategoryReviewId(int categoryReviewId) {
        this.categoryReviewId = categoryReviewId;
    }

    public Float getRating() {
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
