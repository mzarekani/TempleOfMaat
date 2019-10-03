package templeofmaat.judgment;


import templeofmaat.judgment.ReviewService.ReviewService;

public enum ReviewType {
    SELECT("Select Review Type...", null, null),
    BOOK("Book", R.layout.review_book, R.id.review_book),
    NOTE("Note/General", R.layout.review_note, R.id.review_note);

    private String displayName;
    private Integer layoutId;
    private Integer viewId;

    ReviewType(String displayName, Integer layoutId, Integer viewId) {
        this.displayName = displayName;
        this.layoutId = layoutId;
        this.viewId = viewId;
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

    @Override
    public String toString() {
        return displayName;
    }
}