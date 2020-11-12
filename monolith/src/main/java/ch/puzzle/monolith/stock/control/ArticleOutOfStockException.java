package ch.puzzle.monolith.stock.control;

public class ArticleOutOfStockException extends Exception {

    ArticleOutOfStockException(String message) {
        super(message);
    }
}
