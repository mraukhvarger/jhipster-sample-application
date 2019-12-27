package ru.raukh.micro.jhipstersample.web.rest;

import ru.raukh.micro.jhipstersample.domain.Books;
import ru.raukh.micro.jhipstersample.service.BooksService;
import ru.raukh.micro.jhipstersample.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link ru.raukh.micro.jhipstersample.domain.Books}.
 */
@RestController
@RequestMapping("/api")
public class BooksResource {

    private final Logger log = LoggerFactory.getLogger(BooksResource.class);

    private static final String ENTITY_NAME = "sampleServiceJHipsterBooks";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BooksService booksService;

    public BooksResource(BooksService booksService) {
        this.booksService = booksService;
    }

    /**
     * {@code POST  /books} : Create a new books.
     *
     * @param books the books to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new books, or with status {@code 400 (Bad Request)} if the books has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/books")
    public ResponseEntity<Books> createBooks(@Valid @RequestBody Books books) throws URISyntaxException {
        log.debug("REST request to save Books : {}", books);
        if (books.getId() != null) {
            throw new BadRequestAlertException("A new books cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Books result = booksService.save(books);
        return ResponseEntity.created(new URI("/api/books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /books} : Updates an existing books.
     *
     * @param books the books to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated books,
     * or with status {@code 400 (Bad Request)} if the books is not valid,
     * or with status {@code 500 (Internal Server Error)} if the books couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/books")
    public ResponseEntity<Books> updateBooks(@Valid @RequestBody Books books) throws URISyntaxException {
        log.debug("REST request to update Books : {}", books);
        if (books.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Books result = booksService.save(books);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, books.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /books} : get all the books.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of books in body.
     */
    @GetMapping("/books")
    public ResponseEntity<List<Books>> getAllBooks(Pageable pageable) {
        log.debug("REST request to get a page of Books");
        Page<Books> page = booksService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /books/:id} : get the "id" books.
     *
     * @param id the id of the books to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the books, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<Books> getBooks(@PathVariable Long id) {
        log.debug("REST request to get Books : {}", id);
        Optional<Books> books = booksService.findOne(id);
        return ResponseUtil.wrapOrNotFound(books);
    }

    /**
     * {@code DELETE  /books/:id} : delete the "id" books.
     *
     * @param id the id of the books to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBooks(@PathVariable Long id) {
        log.debug("REST request to delete Books : {}", id);
        booksService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/books?query=:query} : search for the books corresponding
     * to the query.
     *
     * @param query the query of the books search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/books")
    public ResponseEntity<List<Books>> searchBooks(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Books for query {}", query);
        Page<Books> page = booksService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
