package ch.puzzle.monolith.orders.control;

import ch.puzzle.monolith.orders.entity.Order;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OrderService {

    public List<Order> findAll() {
        return Order.listAll();
    }
}
