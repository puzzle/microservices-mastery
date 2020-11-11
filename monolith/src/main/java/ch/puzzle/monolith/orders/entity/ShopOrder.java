package ch.puzzle.monolith.orders.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.util.List;

@Entity
public class ShopOrder extends PanacheEntity {
    @OneToMany
    private List<Article> articles;

    @Enumerated(EnumType.STRING)
    private ShopOrderStatus status;
}
