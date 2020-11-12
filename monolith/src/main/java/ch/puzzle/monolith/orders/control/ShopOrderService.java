package ch.puzzle.monolith.orders.control;

import ch.puzzle.monolith.orders.entity.Article;
import ch.puzzle.monolith.orders.entity.ShopOrder;
import ch.puzzle.monolith.orders.entity.ShopOrderDTO;
import ch.puzzle.monolith.orders.entity.ShopOrderStatus;
import ch.puzzle.monolith.stock.control.ArticleOutOfStockException;
import ch.puzzle.monolith.stock.control.ArticleStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public ShopOrder createOrder(ShopOrderDTO shopOrderDTO) throws ArticleOutOfStockException {
        ShopOrder shopOrder = new ShopOrder();
        List<Article> articles = new ArrayList<>();
        for (Long articleId : shopOrderDTO.articles) {
            Object article = Article.findById(articleId);
            articleStockService.orderArticle(articleId);
            articles.add((Article) article);
        }
        shopOrder.setArticles(articles);
        shopOrder.setStatus(ShopOrderStatus.NEW);
        return shopOrder;
    }
}
