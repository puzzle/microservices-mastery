package ch.puzzle.mm.debezium.order.boundary;

import ch.puzzle.mm.debezium.order.control.ShopOrderService;
import ch.puzzle.mm.debezium.order.entity.ShopOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
@Path("/shop-orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShopOrderResource {

    private final Logger log = LoggerFactory.getLogger(ShopOrderResource.class.getName());

    private static int successfulOrders = 0;

    @Inject
    ShopOrderService shopOrderService;

    @GET
    public List<ShopOrder> listAll() {
        return shopOrderService.listAll();
    }


}