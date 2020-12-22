package ch.puzzle.mm.rest.order.boundary;

import ch.puzzle.mm.rest.order.control.ShopOrderService;
import ch.puzzle.mm.rest.order.entity.ShopOrder;
import ch.puzzle.mm.rest.order.entity.ShopOrderDTO;
import ch.puzzle.mm.rest.order.entity.ShopOrderStatus;
import ch.puzzle.mm.rest.stock.boundary.ArticleStockService;
import ch.puzzle.mm.rest.util.ForceFail;
import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ParticipantStatus;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;
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
    private static int failedOrders = 0;

    @Inject
    @RestClient
    ArticleStockService articleStockService;

    @Inject
    ShopOrderService shopOrderService;

    @GET
    public List<ShopOrder> listAll() {
        return ShopOrder.listAll();
    }

    @POST
    @Counted(name = "rest_order_create_request", absolute = true, description = "number of orders requested", tags = {"application=rest-order", "resource=ShopOrderResource"})
    @Timed(name = "rest_order_create_timer", description = "timer for processing a order creation", tags = {"application=rest-order", "resource=ShopOrderResource"})
    @Transactional
    @LRA(value = LRA.Type.REQUIRES_NEW,
            cancelOn = {
                    Response.Status.INTERNAL_SERVER_ERROR // cancel on a 500 code
            },
            cancelOnFamily = {
                    Response.Status.Family.CLIENT_ERROR // cancel on any 4xx code
            })
    public Response createShopOrder(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) String lraUrl,
                                    @HeaderParam(LRA.LRA_HTTP_RECOVERY_HEADER) String recoveryHeader,
                                    @QueryParam("fail") ForceFail forceFail,
                                    ShopOrderDTO shopOrderDTO) {

        String lraId = getLraId(lraUrl);
        log.info("----------------------------");
        log.info("Create shopOrder: {}", lraUrl);
        log.info("LRA Transaction Id: {}", lraId);
        log.info("Recovery Header: {}", recoveryHeader);
        log.info("----------------------------");

        // create ShopOrder locally
        ShopOrder shopOrder = shopOrderService.createOrder(shopOrderDTO, lraId);

        // call remote service
        articleStockService.orderArticles(forceFail, shopOrderDTO.articleOrders);

        // Failure simulation
        if (forceFail == ForceFail.ORDER) {
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        } else {
            return Response.ok(shopOrder).build();
        }
    }

    @Counted(name = "rest_order_create_success", absolute = true, description = "number of orders successful", tags = {"application=rest-order", "resource=ShopOrderResource"})
    public int successfulOrder() {
        return successfulOrders;
    }

    @Counted(name = "rest_order_create_failed", absolute = true, description = "number of orders failed", tags = {"application=rest-order", "resource=ShopOrderResource"})
    public int compensatedOrder() {
        return failedOrders;
    }

    @PUT
    @Path("/complete")
    @Complete
    @Consumes(MediaType.TEXT_PLAIN)
    @Transactional
    public Response completeShopOrder(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) String lraUrl) {
        String lraId = getLraId(lraUrl);

        log.info("----------------------------");
        log.info("Complete ShopOrder");
        log.info("LRA Transaction Id: {}", lraId);
        log.info("----------------------------");

        int updated = ShopOrder.setStatusByLra(ShopOrderStatus.COMPLETED, lraId);
        successfulOrders += updated;

        return Response.ok(ParticipantStatus.Completed.name()).build();
    }

    @PUT
    @Path("/compensate")
    @Compensate
    @Consumes(MediaType.TEXT_PLAIN)
    @Transactional
    public Response compensateShopOrder(@HeaderParam(LRA.LRA_HTTP_CONTEXT_HEADER) String lraUrl) {
        String lraId = getLraId(lraUrl);

        log.info("----------------------------");
        log.info("Compensate ShopOrder");
        log.info("LRA Transaction Id: {}", lraId);
        log.info("----------------------------");

        int updated = ShopOrder.setStatusByLra(ShopOrderStatus.INCOMPLETE, lraId);
        failedOrders += updated;

        return Response.ok(ParticipantStatus.Compensated.name()).build();
    }

    String getLraId(String longRunningHeader) {
        return longRunningHeader.substring(longRunningHeader.lastIndexOf('/') + 1);
    }
}