package ch.puzzle.mm.kafka.stock.exception;

public class ArticleOutOfStockException extends Exception {

    public ArticleOutOfStockException(String message) {
        super(message);
    }
}
