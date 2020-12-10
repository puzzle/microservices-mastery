package ch.puzzle.mm.kafka.order.entity;

import ch.puzzle.mm.kafka.article.entity.Article;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
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
}
