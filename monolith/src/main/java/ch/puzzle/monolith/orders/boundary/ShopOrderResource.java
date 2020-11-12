package ch.puzzle.monolith.orders.boundary;

import ch.puzzle.monolith.orders.control.ShopOrderService;
import ch.puzzle.monolith.orders.entity.ShopOrder;
import ch.puzzle.monolith.orders.entity.ShopOrderDTO;
import ch.puzzle.monolith.stock.control.ArticleOutOfStockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Inject
    ShopOrderService shopOrderService;

    @GET
    public List<ShopOrder> listAll() {
        return shopOrderService.listAll();
    }

    @POST
    @Transactional
    public Response createShopOrder(ShopOrderDTO shopOrderDTO) {
        try {
            ShopOrder shopOrder = shopOrderService.createOrder(shopOrderDTO);
            shopOrder.persist();
            return Response.ok(shopOrder).build();
        } catch (ArticleOutOfStockException e) {
            log.error(e.getMessage());
            return Response.serverError().header("message", e.getMessage()).build();
        }
    }
}