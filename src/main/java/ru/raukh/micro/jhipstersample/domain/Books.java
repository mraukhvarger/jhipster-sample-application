package ru.raukh.micro.jhipstersample.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Books.
 */
@Entity
@Table(name = "books")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "books")
public class Books implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "book_name", nullable = false)
    private String bookName;

    @OneToMany(mappedBy = "books")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Genres> genres = new HashSet<>();

    @ManyToMany(mappedBy = "books")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Authors> authors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public Books bookName(String bookName) {
        this.bookName = bookName;
        return this;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Set<Genres> getGenres() {
        return genres;
    }

    public Books genres(Set<Genres> genres) {
        this.genres = genres;
        return this;
    }

    public Books addGenre(Genres genres) {
        this.genres.add(genres);
        genres.setBooks(this);
        return this;
    }

    public Books removeGenre(Genres genres) {
        this.genres.remove(genres);
        genres.setBooks(null);
        return this;
    }

    public void setGenres(Set<Genres> genres) {
        this.genres = genres;
    }

    public Set<Authors> getAuthors() {
        return authors;
    }

    public Books authors(Set<Authors> authors) {
        this.authors = authors;
        return this;
    }

    public Books addAuthor(Authors authors) {
        this.authors.add(authors);
        authors.getBooks().add(this);
        return this;
    }

    public Books removeAuthor(Authors authors) {
        this.authors.remove(authors);
        authors.getBooks().remove(this);
        return this;
    }

    public void setAuthors(Set<Authors> authors) {
        this.authors = authors;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Books)) {
            return false;
        }
        return id != null && id.equals(((Books) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Books{" +
            "id=" + getId() +
            ", bookName='" + getBookName() + "'" +
            "}";
    }
}
