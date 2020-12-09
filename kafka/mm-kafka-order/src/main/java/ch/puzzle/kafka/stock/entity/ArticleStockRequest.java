package ch.puzzle.kafka.stock.entity;

import ch.puzzle.kafka.order.entity.ShopOrderDTO;

public class ArticleStockRequest {
    public ShopOrderDTO shopOrderDTO;

    public ArticleStockRequest(ShopOrderDTO shopOrderDTO) {
        this.shopOrderDTO = shopOrderDTO;
    }

}
