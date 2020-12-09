package ch.puzzle.kafka.order.control;

import ch.puzzle.kafka.order.entity.ShopOrder;
import ch.puzzle.kafka.order.entity.ShopOrderDTO;
import ch.puzzle.kafka.stock.boundary.ArticleStockRequestProducer;
import ch.puzzle.kafka.stock.entity.ArticleStockRequest;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ShopOrderService {

    @Inject
    @RestClient
    ArticleStockRequestProducer articleStockRequestProducer;

    public List<ShopOrder> listAll() {
        return ShopOrder.listAll();
    }

    @Traced
    public ShopOrder createOrder(ShopOrderDTO shopOrderDTO) {
        ShopOrder shopOrder = new ShopOrder();
        articleStockRequestProducer.createRequest(shopOrderDTO);

//        if(stockResponse == null || stockResponse.getStatus() < 200 || stockResponse.getStatus() >= 300) {
//            throw new WebApplicationException("Exception in ArticleStockService. HttpResponseCode: " + stockResponse.getStatus());
//        }
//
//        List<Article> articles = new ArrayList<>();
//        for (ArticleOrderDTO articleOrder : shopOrderDTO.articleOrders) {
//            Object article = Article.findById(articleOrder.articleId);
//            articles.add((Article) article);
//        }
//
//        shopOrder.setArticles(articles);
//        shopOrder.setStatus(ShopOrderStatus.NEW);
        return shopOrder;
    }
}
