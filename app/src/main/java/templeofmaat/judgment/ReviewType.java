package templeofmaat.judgment;

public enum ReviewType {
    SELECT("Select Review Type..."),
    Book("Book"),
    NOTE("Note/General");

    private String displayName;

    ReviewType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}