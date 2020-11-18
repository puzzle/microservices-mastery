package ch.puzzle.rest.order.control;

import ch.puzzle.rest.order.entity.ShopOrder;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Traced
public class ShopOrderRepository implements PanacheRepository<ShopOrder> {
}
