package ch.puzzle.rest.stock.control;
public class ArticleOutOfStockException extends Exception {

    ArticleOutOfStockException(String message) {
        super(message);
    }
}
