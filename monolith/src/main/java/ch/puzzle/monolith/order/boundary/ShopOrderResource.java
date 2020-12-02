package ch.puzzle.monolith.order.boundary;

import ch.puzzle.monolith.order.control.ShopOrderService;
import ch.puzzle.monolith.order.entity.ShopOrder;
import ch.puzzle.monolith.order.entity.ShopOrderDTO;
import ch.puzzle.monolith.exception.ArticleOutOfStockException;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    @POST
    @Transactional
    @Counted(name = "monolith_order_create_request", absolute = true, description = "number of orders requested", tags = {"application=monolith", "resource=ShopOrderResource"})
    @Timed(name = "monolith_order_create_timer", description = "timer for processing a order creation", tags = {"application=monolith", "resource=ShopOrderResource"})
    public Response createShopOrder(ShopOrderDTO shopOrderDTO) throws ArticleOutOfStockException {
        ShopOrder shopOrder = shopOrderService.createOrder(shopOrderDTO);
        shopOrder.persist();
        registerSuccessfulOrder();
        return Response.ok(shopOrder).build();
    }

    @Counted(name = "monolith_order_create_success", absolute = true, description = "number of orders successful", tags = {"application=monolith", "resource=ShopOrderResource"})
    public int registerSuccessfulOrder() {
        return successfulOrders++;
    }
}