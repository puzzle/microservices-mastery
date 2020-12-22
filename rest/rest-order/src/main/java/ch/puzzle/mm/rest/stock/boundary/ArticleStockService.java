package ch.puzzle.mm.rest.stock.boundary;

import ch.puzzle.mm.rest.util.ForceFail;
import ch.puzzle.mm.rest.order.entity.ArticleOrderDTO;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/article-stocks")
@RegisterRestClient(configKey = "article-stock-api")
public interface ArticleStockService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response orderArticles(@QueryParam("fail") ForceFail fail, List<ArticleOrderDTO> orders);
}
