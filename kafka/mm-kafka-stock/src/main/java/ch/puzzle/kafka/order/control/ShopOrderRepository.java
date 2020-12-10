package ch.puzzle.kafka.order.control;

import ch.puzzle.kafka.order.entity.ShopOrder;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Traced
public class ShopOrderRepository implements PanacheRepository<ShopOrder> {
}
