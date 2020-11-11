package ch.puzzle.monolith.orders.boundary;

import ch.puzzle.monolith.orders.control.ShopOrderService;
import ch.puzzle.monolith.orders.entity.ShopOrder;

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

    @Inject
    ShopOrderService shopOrderService;

    @GET
    public List<ShopOrder> hello() {
        return shopOrderService.listAll();
    }

    @POST
    @Transactional
    public Response createShopOrder(ShopOrder shopOrder) {
        shopOrder.persist();
        return Response.ok(shopOrder).build();
    }
}