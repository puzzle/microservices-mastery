package ch.puzzle.monolith.orders.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity
public class Article extends PanacheEntity {

    private String name;
    private Double price;
}
