package ru.raukh.micro.jhipstersample.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Authors.
 */
@Entity
@Table(name = "authors")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "authors")
public class Authors implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "author_name", nullable = false)
    private String authorName;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "authors_book",
               joinColumns = @JoinColumn(name = "authors_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"))
    private Set<Books> books = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Authors authorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Set<Books> getBooks() {
        return books;
    }

    public Authors books(Set<Books> books) {
        this.books = books;
        return this;
    }

    public Authors addBook(Books books) {
        this.books.add(books);
        books.getAuthors().add(this);
        return this;
    }

    public Authors removeBook(Books books) {
        this.books.remove(books);
        books.getAuthors().remove(this);
        return this;
    }

    public void setBooks(Set<Books> books) {
        this.books = books;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Authors)) {
            return false;
        }
        return id != null && id.equals(((Authors) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Authors{" +
            "id=" + getId() +
            ", authorName='" + getAuthorName() + "'" +
            "}";
    }
}
