package ch.puzzle.monolith.orders.boundary;

import ch.puzzle.monolith.monkey.control.ChaosMonkey;
import ch.puzzle.monolith.orders.control.ShopOrderService;
import ch.puzzle.monolith.orders.entity.ShopOrder;
import ch.puzzle.monolith.orders.entity.ShopOrderDTO;
import ch.puzzle.monolith.stock.control.ArticleOutOfStockException;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/shop-orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShopOrderResource {

    private final Logger log = LoggerFactory.getLogger(ShopOrderResource.class.getName());

    private int successfulOrders = 0;

    @Inject
    ShopOrderService shopOrderService;

    @GET
    @ChaosMonkey
    public List<ShopOrder> listAll() {
        return shopOrderService.listAll();
    }

    @POST
    @Transactional
    @Counted(name = "monolith_order_create_request", absolute = true, description = "number of orders requested", tags = {"application=monolith", "resource=ShopOrderResource"})
    @Timed(name = "monolith_order_create_timer", description = "timer for processing a order creation", tags = {"application=monolith", "resource=ShopOrderResource"})
    @ChaosMonkey
    public Response createShopOrder(ShopOrderDTO shopOrderDTO) {
        try {
            ShopOrder shopOrder = shopOrderService.createOrder(shopOrderDTO);
            shopOrder.persist();
            successfulOrders++;
            return Response.ok(shopOrder).build();
        } catch (ArticleOutOfStockException e) {
            log.error(e.getMessage());
            return Response.serverError().header("message", e.getMessage()).build();
        }
    }

    @Counted(name = "monolith_order_create_success", absolute = true, description = "number of orders successful", tags = {"application=monolith", "resource=ShopOrderResource"})
    public int getSuccessfulOrders() {
        return successfulOrders;
    }
}