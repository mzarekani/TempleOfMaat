package templeofmaat.judgment.data;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.Instant;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = Note.TABLE_NAME,
        indices = {@Index(Note.COLUMN_CATEGORY_REVIEW_ID)},
        foreignKeys = @ForeignKey(entity = CategoryReview.class, parentColumns = "id",
                childColumns = Note.COLUMN_CATEGORY_REVIEW_ID, onDelete = CASCADE, onUpdate = CASCADE))
public class Note {

    static final String TABLE_NAME = "note";
    static final String COLUMN_CREATE_TIME = "create_time";
    static final String COLUMN_UPDATE_TIME = "update_time";
    static final String COLUMN_CATEGORY_REVIEW_ID = "category_review_id";
    static final String COLUMN_COMMENT = "comment";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = Note.COLUMN_CREATE_TIME)
    @NonNull
    private Instant createTime;

    @ColumnInfo(name = Note.COLUMN_UPDATE_TIME)
    @NonNull
    private Instant updateTime;

    @ColumnInfo(name = Note.COLUMN_CATEGORY_REVIEW_ID)
    private int categoryReviewId;

    @ColumnInfo(name = Note.COLUMN_COMMENT)
    private String comment;

    public Note(String comment) {
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
