package ch.puzzle.kafka.order.control;

import ch.puzzle.kafka.article.entity.Article;
import ch.puzzle.kafka.order.entity.ShopOrder;
import ch.puzzle.kafka.order.entity.ShopOrderDTO;
import ch.puzzle.kafka.order.entity.ShopOrderStatus;
import ch.puzzle.kafka.stock.boundary.ArticleStockRequestProducer;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ShopOrderService {

    @Inject
    ArticleStockRequestProducer articleStockRequestProducer;

    public List<ShopOrder> listAll() {
        return ShopOrder.listAll();
    }

    @Traced
    public ShopOrder createOrder(ShopOrderDTO shopOrderDTO) {
        List<Long> ids = shopOrderDTO.articleOrders.stream().map(s -> s.articleId).collect(Collectors.toList());
        List<Article> articles = Article.list("id in ?1", ids);

        // store order to shopOrder table
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.setStatus(ShopOrderStatus.NEW);
        shopOrder.setArticles(articles);
        shopOrder.persist();

        // fire event
        articleStockRequestProducer.createRequest(shopOrderDTO);

        return shopOrder;
    }
}
