package ch.puzzle.monolith.orders.control;

import ch.puzzle.monolith.orders.entity.ShopOrder;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ShopOrderService {

    public List<ShopOrder> listAll() {
        return ShopOrder.listAll();
    }
}
