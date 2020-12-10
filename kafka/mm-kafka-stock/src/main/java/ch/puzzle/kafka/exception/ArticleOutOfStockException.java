package ch.puzzle.kafka.exception;

public class ArticleOutOfStockException extends Exception {

    public ArticleOutOfStockException(String message) {
        super(message);
    }
}
