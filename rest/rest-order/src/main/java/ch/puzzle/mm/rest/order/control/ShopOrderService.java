package ch.puzzle.mm.rest.order.control;

import ch.puzzle.mm.rest.order.entity.*;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ShopOrderService {

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public ShopOrder createOrder(ShopOrderDTO dto, String lra) {
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.setStatus(ShopOrderStatus.NEW);

        // set lra id
        shopOrder.setLra(lra);

        // create order
        List<ArticleOrder> articleOrders = dto.articleOrders.stream()
                .map(s -> new ArticleOrder(s.articleId, s.amount))
                .collect(Collectors.toList());

        shopOrder.setArticleOrders(articleOrders);
        shopOrder.persistAndFlush();

        return shopOrder;
    }
}