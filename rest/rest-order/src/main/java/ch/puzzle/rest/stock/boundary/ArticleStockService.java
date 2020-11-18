package ch.puzzle.rest.stock.boundary;

import ch.puzzle.rest.order.entity.ArticleOrderDTO;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/article-stocks")
@RegisterRestClient(configKey = "article-stock-api")
public interface ArticleStockService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    Response orderArticles(List<ArticleOrderDTO> orders);
}


