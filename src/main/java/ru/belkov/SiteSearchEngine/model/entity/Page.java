package ru.belkov.SiteSearchEngine.model.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Page {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "path", nullable = false, length = -1)
    private String path;
    @Basic
    @Column(name = "code", nullable = false)
    private Integer code;
    @Basic
    @Column(name = "content", nullable = false, length = -1)
    private String content;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equals(id, page.id) && Objects.equals(path, page.path) && Objects.equals(code, page.code) && Objects.equals(content, page.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path, code, content);
    }
}
