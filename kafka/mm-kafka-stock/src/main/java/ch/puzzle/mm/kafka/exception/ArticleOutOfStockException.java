package ch.puzzle.mm.kafka.exception;

public class ArticleOutOfStockException extends Exception {

    public ArticleOutOfStockException(String message) {
        super(message);
    }
}
