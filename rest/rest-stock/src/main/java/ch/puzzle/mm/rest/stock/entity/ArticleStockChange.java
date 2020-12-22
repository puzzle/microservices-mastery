package ch.puzzle.mm.rest.stock.entity;

import ch.puzzle.mm.rest.article.entity.Article;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class ArticleStockChange extends PanacheEntity {
    @ManyToOne(targetEntity = Article.class)
    private Article article;

    @Column(nullable = false)
    private String lra;

    private int count;

    public ArticleStockChange(Article article, int count, String lraId) {
        this.article = article;
        this.count = count;
        this.lra = lraId;
    }

    public ArticleStockChange() {
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLra() {
        return lra;
    }

    public void setLra(String lraId) {
        this.lra = lraId;
    }

    public static List<ArticleStockChange> findByLraId(String lraId) {
        return list("lra", lraId);
    }
}