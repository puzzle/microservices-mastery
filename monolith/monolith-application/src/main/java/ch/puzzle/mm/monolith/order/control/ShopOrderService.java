package ch.puzzle.mm.monolith.order.control;

import ch.puzzle.mm.monolith.article.entity.Article;
import ch.puzzle.mm.monolith.exception.StockIncompleteException;
import ch.puzzle.mm.monolith.order.entity.*;
import ch.puzzle.mm.monolith.stock.control.ArticleStockService;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ShopOrderService {

    @Inject
    ArticleStockService articleStockService;

    public List<ShopOrder> listAll() {
        return ShopOrder.listAll();
    }

    @Traced
    public ShopOrder createOrder(ShopOrderDTO shopOrderDTO) {
        ShopOrder shopOrder = new ShopOrder();

        List<ArticleOrder> articleOrders = shopOrderDTO.articleOrders.stream()
                .filter(a -> Article.findByIdOptional(a.articleId).isPresent())
                .map(s -> new ArticleOrder(s.articleId, s.amount))
                .collect(Collectors.toList());

        try {
            articleStockService.orderArticles(articleOrders);
            shopOrder.setStatus(ShopOrderStatus.COMPLETED);
        } catch(StockIncompleteException e) {
            shopOrder.setStatus(ShopOrderStatus.INCOMPLETE);
        }
        shopOrder.setArticleOrders(articleOrders);
        shopOrder.persist();

        return shopOrder;
    }
}
