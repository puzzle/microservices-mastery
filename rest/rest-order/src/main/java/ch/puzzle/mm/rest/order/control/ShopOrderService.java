package ch.puzzle.mm.rest.order.control;

import ch.puzzle.mm.rest.order.entity.*;
import ch.puzzle.mm.rest.stock.boundary.ArticleStockService;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ShopOrderService {

    @Inject
    @RestClient
    ArticleStockService articleStockService;

    public List<ShopOrder> listAll() {
        return ShopOrder.listAll();
    }

    @Traced
    public ShopOrder createOrder(ShopOrderDTO shopOrderDTO) {
        ShopOrder shopOrder = new ShopOrder();

        List<Long> ids = shopOrderDTO.articleOrders.stream().map(s -> s.articleId).collect(Collectors.toList());
        List<ArticleOrder> articleOrders = ArticleOrder.list("articleid in ?1", ids);

        shopOrder.setArticleOrders(articleOrders);
        shopOrder.setStatus(ShopOrderStatus.NEW);
        shopOrder.persistAndFlush();

        Response stockResponse = articleStockService.orderArticles(shopOrderDTO.articleOrders);

        if(stockResponse == null || stockResponse.getStatus() < 200 || stockResponse.getStatus() >= 300) {
            throw new WebApplicationException("Exception in ArticleStockService. HttpResponseCode: " + (stockResponse != null ? stockResponse.getStatus() : "unknown"));
        }

        shopOrder.setStatus(ShopOrderStatus.COMPLETED);
        shopOrder.persistAndFlush();
        return shopOrder;
    }
}
