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
    @OneToMany(mappedBy = "lemma", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<Index> indexes;
    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

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

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lemma)) return false;
        Lemma lemma1 = (Lemma) o;
        return Objects.equals(lemma, lemma1.lemma) && Objects.equals(site, lemma1.site);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lemma, site);
    }

    @Override
    public String toString() {
        return "Lemma{" +
                "id=" + id +
                ", lemma='" + lemma + '\'' +
                ", frequency=" + frequency +
                ", indexes=" + indexes +
                ", site=" + site +
                '}';
    }
}
