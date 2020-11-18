package ch.puzzle.monolith.stock.control;

import javax.ws.rs.WebApplicationException;

public class ArticleOutOfStockException extends WebApplicationException {

    ArticleOutOfStockException(String message) {
        super(message);
    }
}
