package ch.puzzle.monolith.orders.control;

import ch.puzzle.monolith.article.control.ArticleService;
import ch.puzzle.monolith.article.entity.Article;
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

    @Inject
    ArticleService articleService;

    public List<ShopOrder> listAll() {
        return ShopOrder.listAll();
    }

    @Traced
    public ShopOrder createOrder(ShopOrderDTO shopOrderDTO) throws ArticleOutOfStockException {
        ShopOrder shopOrder = new ShopOrder();
        List<Article> articles = new ArrayList<>();
        for (ArticleOrderDTO articleOrder : shopOrderDTO.articleOrders) {
            Article article = articleService.findById(articleOrder.articleId);
            articleStockService.orderArticle(articleOrder.articleId, articleOrder.amount);
            articles.add(article);
        }
        shopOrder.setArticles(articles);
        shopOrder.setStatus(ShopOrderStatus.NEW);
        return shopOrder;
    }
}
