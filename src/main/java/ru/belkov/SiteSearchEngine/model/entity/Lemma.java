package ru.belkov.SiteSearchEngine.model.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Lemma {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "lemma")
    private String lemma;

    @Basic
    @Column(name = "frequency")
    private Integer frequency;

    @OneToMany(mappedBy = "lemma", fetch = FetchType.LAZY)
    private Collection<Index> indexes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Collection<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(Collection<Index> indicesById) {
        this.indexes = indicesById;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lemma lemma1 = (Lemma) o;
        return Objects.equals(lemma, lemma1.lemma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lemma);
    }
}
