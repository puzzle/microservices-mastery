package ch.puzzle.monolith.orders.control;

import ch.puzzle.monolith.orders.entity.ShopOrder;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderService {

    public List<ShopOrder> findAll() {
        return ShopOrder.listAll();
    }
}
