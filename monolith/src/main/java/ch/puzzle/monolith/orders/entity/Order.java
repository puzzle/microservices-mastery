package ch.puzzle.monolith.orders.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Order extends PanacheEntity {
    @OneToMany(targetEntity = Article.class, cascade = CascadeType.REMOVE, mappedBy = "id", orphanRemoval = true)
    private List<Article> articles;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

}
