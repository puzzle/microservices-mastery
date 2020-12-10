package ch.puzzle.mm.monolith.order.control;

import ch.puzzle.mm.monolith.article.control.ArticleService;
import ch.puzzle.mm.monolith.article.entity.Article;
import ch.puzzle.mm.monolith.order.entity.*;
import ch.puzzle.mm.monolith.exception.ArticleOutOfStockException;
import ch.puzzle.mm.monolith.stock.control.ArticleStockService;
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
