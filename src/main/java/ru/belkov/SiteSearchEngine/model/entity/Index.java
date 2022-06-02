package ru.belkov.SiteSearchEngine.model.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "search_index")
public class Index {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", referencedColumnName = "id", nullable = false)
    private Page page;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id", referencedColumnName = "id", nullable = false)
    private Lemma lemma;

    @Basic
    @Column(name = "search_rank")
    private Double rank;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getRank() {
        return rank;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page pageByPageId) {
        this.page = pageByPageId;
    }

    public Lemma getLemma() {
        return lemma;
    }

    public void setLemma(Lemma lemmaByLemmaId) {
        this.lemma = lemmaByLemmaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Index)) return false;
        Index index = (Index) o;
        return page.equals(index.page) && lemma.equals(index.lemma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, lemma);
    }
}
