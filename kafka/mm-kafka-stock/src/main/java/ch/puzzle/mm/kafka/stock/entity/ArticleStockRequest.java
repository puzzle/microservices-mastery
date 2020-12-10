package ch.puzzle.mm.kafka.stock.entity;

import ch.puzzle.mm.kafka.order.entity.ShopOrderDTO;

public class ArticleStockRequest {
    public ShopOrderDTO shopOrderDTO;

    public ArticleStockRequest(ShopOrderDTO shopOrderDTO) {
        this.shopOrderDTO = shopOrderDTO;
    }

}
