package templeofmaat.judgment;

public enum CategoryType {
    SELECT("Select Category Type..."),
    REVIEW("Review"),
    NOTE("Note");

    private String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
