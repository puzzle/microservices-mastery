package ch.puzzle.monolith.orders.control;

import ch.puzzle.monolith.orders.entity.*;
import ch.puzzle.monolith.stock.control.ArticleOutOfStockException;
import ch.puzzle.monolith.stock.control.ArticleStockService;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ShopOrderService {

    @Inject
    ArticleStockService articleStockService;

    public List<ShopOrder> listAll() {
        return ShopOrder.listAll();
    }

    @Traced(operationName = "ShopOrderService::createOrder")
    public ShopOrder createOrder(ShopOrderDTO shopOrderDTO) throws ArticleOutOfStockException {
        ShopOrder shopOrder = new ShopOrder();
        List<Article> articles = new ArrayList<>();
        for (ArticleOrderDTO articleOrder : shopOrderDTO.articleOrders) {
            Object article = Article.findById(articleOrder.articleId);
            articleStockService.orderArticle(articleOrder.articleId, articleOrder.amount);
            articles.add((Article) article);
        }
        shopOrder.setArticles(articles);
        shopOrder.setStatus(ShopOrderStatus.NEW);
        return shopOrder;
    }
}
