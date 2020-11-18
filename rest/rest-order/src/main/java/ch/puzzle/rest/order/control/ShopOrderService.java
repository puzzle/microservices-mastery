package ch.puzzle.rest.order.control;

import ch.puzzle.rest.article.entity.Article;
import ch.puzzle.rest.order.entity.ArticleOrderDTO;
import ch.puzzle.rest.order.entity.ShopOrder;
import ch.puzzle.rest.order.entity.ShopOrderDTO;
import ch.puzzle.rest.order.entity.ShopOrderStatus;
import ch.puzzle.rest.stock.boundary.ArticleStockService;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

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
        Response stockResponse = articleStockService.orderArticles(shopOrderDTO.articleOrders);

        if(stockResponse == null || stockResponse.getStatus() < 200 || stockResponse.getStatus() >= 300) {
            throw new WebApplicationException("Exception in ArticleStockService. HttpResponseCode: " + stockResponse.getStatus());
        }

        List<Article> articles = new ArrayList<>();
        for (ArticleOrderDTO articleOrder : shopOrderDTO.articleOrders) {
            Object article = Article.findById(articleOrder.articleId);
            articles.add((Article) article);
        }

        shopOrder.setArticles(articles);
        shopOrder.setStatus(ShopOrderStatus.NEW);
        return shopOrder;
    }
}
