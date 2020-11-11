package ch.puzzle.monolith.orders.boundary;

import ch.puzzle.monolith.orders.control.OrderService;
import ch.puzzle.monolith.orders.entity.Order;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderService orderService;

    @GET
    public List<Order> hello() {
        return orderService.findAll();
    }
}