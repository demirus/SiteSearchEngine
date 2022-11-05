package ru.belkov.SiteSearchEngine.model.entity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import javax.persistence.*;
import java.util.Objects;
import javax.persistence.Index;

@Entity
@Table(indexes = @Index(columnList = "path"))
public class Page {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic
    @Column(name = "path", nullable = false)
    private String path;

    @Basic
    @Column(name = "code", nullable = false)
    private Integer code;

    @Basic
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    public String getTitle() {
        Document document = Jsoup.parse(content);
        return document.title();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        if (!(o instanceof Page)) return false;
        Page page = (Page) o;
        return path.equals(page.path) && site.equals(page.site);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, site);
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", code=" + code +
                ", content='" + content + '\'' +
                ", site=" + site +
                '}';
    }
}
