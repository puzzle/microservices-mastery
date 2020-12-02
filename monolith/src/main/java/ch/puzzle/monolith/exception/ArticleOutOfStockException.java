package ch.puzzle.monolith.exception;

public class ArticleOutOfStockException extends Exception {

    public ArticleOutOfStockException(String message) {
        super(message);
    }
}
