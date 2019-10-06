package templeofmaat.judgment.data;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.Instant;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = CategoryReview.TABLE_NAME,
        indices = {@Index(CategoryReview.COLUMN_TITLE),
                @Index(CategoryReview.COLUMN_PARENT_ID)},
        foreignKeys = @ForeignKey(entity = CategoryReview.class, parentColumns = "id",
                childColumns = CategoryReview.COLUMN_PARENT_ID, onDelete = CASCADE, onUpdate = CASCADE))
public class CategoryReview implements Serializable {

    static final String TABLE_NAME = "category_review";
    static final String COLUMN_CREATE_TIME = "create_time";
    static final String COLUMN_UPDATE_TIME = "update_time";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_SUBTITLE = "subtitle";
    static final String COLUMN_IS_CATEGORY = "is_category";
    static final String COLUMN_IS_REVIEW = "is_review";
    static final String COLUMN_REVIEW_TYPE = "review_type";
    static final String COLUMN_PARENT_ID = "parent_id";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = CategoryReview.COLUMN_CREATE_TIME)
    @NonNull
    private Instant createTime;

    @ColumnInfo(name = CategoryReview.COLUMN_UPDATE_TIME)
    @NonNull
    private Instant updateTime;

    @ColumnInfo(name = CategoryReview.COLUMN_TITLE)
    @NonNull
    private String title;

    @ColumnInfo(name = CategoryReview.COLUMN_SUBTITLE)
    private String subtitle;

    @ColumnInfo(name = CategoryReview.COLUMN_IS_CATEGORY)
    private boolean category;

    @ColumnInfo(name = CategoryReview.COLUMN_IS_REVIEW)
    private boolean review;

    @ColumnInfo(name = CategoryReview.COLUMN_REVIEW_TYPE)
    private String reviewType;

    @ColumnInfo(name = CategoryReview.COLUMN_PARENT_ID)
    @Nullable
    private Integer parentId;

    public CategoryReview() {}

    public CategoryReview(@NonNull String title) {
        this.title = title;
        createTime = Instant.now();
        updateTime = Instant.now();
    }

    public CategoryReview(@NonNull String title, @Nullable Integer parentId, boolean category, boolean review, String reviewType) {
        this.title = title;
        this.parentId = parentId;
        this.category = category;
        this.review = review;
        this.reviewType = reviewType;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isCategory() {
        return category;
    }

    public void setCategory(boolean category) {
        this.category = category;
    }

    public boolean isReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return title;
    }
}
