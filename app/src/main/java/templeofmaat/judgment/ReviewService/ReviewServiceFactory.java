package templeofmaat.judgment.ReviewService;

import templeofmaat.judgment.ReviewType;

public class ReviewServiceFactory {
    public static ReviewService getReviewService(ReviewType reviewType) {
        ReviewService reviewService = null;
        switch (reviewType) {
            case BOOK:
                reviewService = new BookService();
                break;
            case NOTE:
                reviewService = new NoteService();
                break;
        }
        return reviewService;
    }
}
