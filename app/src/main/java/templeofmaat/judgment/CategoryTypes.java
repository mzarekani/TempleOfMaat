package templeofmaat.judgment;

public enum CategoryTypes {
    SELECT("Select Category Type..."),
    REVIEW("Review"),
    NOTE("Note");

    private String displayName;

    CategoryTypes(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
