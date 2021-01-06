package ch.puzzle.mm.rest.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Entity
public class ShopOrder extends PanacheEntity {
    @OneToMany(cascade = CascadeType.ALL)
    private List<ArticleOrder> articleOrders;

    @Enumerated(EnumType.STRING)
    private ShopOrderStatus status;

    @NotNull
    @Column(unique=true, nullable = false)
    private String lra;

    public ShopOrder() { }

    public List<ArticleOrder> getArticleOrders() {
        return articleOrders;
    }

    public void setArticleOrders(List<ArticleOrder> articles) {
        this.articleOrders = articles;
    }

    public ShopOrderStatus getStatus() {
        return status;
    }

    public void setStatus(ShopOrderStatus status) {
        this.status = status;
    }

    public String getLra() {
        return lra;
    }

    public void setLra(String lraId) {
        this.lra = lraId;
    }

    public static Optional<ShopOrder> findByLraId(String lraId) {
        return find("lra", lraId).singleResultOptional();
    }

    public static int setStatusByLra(ShopOrderStatus status, String lra) {
        return update("status = :status where lra = :lra",
                Parameters.with("status", status).and("lra", lra));
    }
}