package ch.puzzle.mm.debezium.order.entity;

import ch.puzzle.mm.debezium.article.entity.Article;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.util.List;

@Entity
public class ShopOrder extends PanacheEntity {
    @OneToMany
    private List<Article> articles;

    @Enumerated(EnumType.STRING)
    private ShopOrderStatus status;

    public ShopOrder() {
    }

    public ShopOrder(List<Article> articles, ShopOrderStatus status) {
        this.articles = articles;
        this.status = status;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public ShopOrderStatus getStatus() {
        return status;
    }

    public void setStatus(ShopOrderStatus status) {
        this.status = status;
    }

    public static ShopOrder getByIdOrThrow(long id) throws EntityNotFoundException {
        ShopOrder order = ShopOrder.findById(id);
        if (order == null) {
            throw new EntityNotFoundException();
        }

        return order;
    }
}
