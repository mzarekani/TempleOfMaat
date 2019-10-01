package templeofmaat.judgment;


import templeofmaat.judgment.ReviewService.BookService;
import templeofmaat.judgment.ReviewService.NoteService;
import templeofmaat.judgment.ReviewService.ReviewService;

public enum ReviewType {
    SELECT("Select Review Type...", null, null, null),
    BOOK("Book", R.layout.review_book, R.id.review_book, new BookService()),
    NOTE("Note/General", R.layout.review_note, R.id.review_note, new NoteService());

    private String displayName;
    private Integer layoutId;
    private Integer viewId;
    private ReviewService reviewService;

    ReviewType(String displayName, Integer layoutId, Integer viewId, ReviewService reviewService) {
        this.displayName = displayName;
        this.layoutId = layoutId;
        this.viewId = viewId;
        this.reviewService = reviewService;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Integer getLayoutId() {
        return layoutId;
    }

    public Integer getViewId() {
        return viewId;
    }

    public ReviewService getService() {
        return reviewService;
    }

    @Override
    public String toString() {
        return displayName;
    }
}