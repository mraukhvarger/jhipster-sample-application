package ru.raukh.micro.jhipstersample.web.rest;

import ru.raukh.micro.jhipstersample.domain.Authors;
import ru.raukh.micro.jhipstersample.service.AuthorsService;
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
 * REST controller for managing {@link ru.raukh.micro.jhipstersample.domain.Authors}.
 */
@RestController
@RequestMapping("/api")
public class AuthorsResource {

    private final Logger log = LoggerFactory.getLogger(AuthorsResource.class);

    private static final String ENTITY_NAME = "sampleServiceJHipsterAuthors";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AuthorsService authorsService;

    public AuthorsResource(AuthorsService authorsService) {
        this.authorsService = authorsService;
    }

    /**
     * {@code POST  /authors} : Create a new authors.
     *
     * @param authors the authors to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new authors, or with status {@code 400 (Bad Request)} if the authors has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/authors")
    public ResponseEntity<Authors> createAuthors(@Valid @RequestBody Authors authors) throws URISyntaxException {
        log.debug("REST request to save Authors : {}", authors);
        if (authors.getId() != null) {
            throw new BadRequestAlertException("A new authors cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Authors result = authorsService.save(authors);
        return ResponseEntity.created(new URI("/api/authors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /authors} : Updates an existing authors.
     *
     * @param authors the authors to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated authors,
     * or with status {@code 400 (Bad Request)} if the authors is not valid,
     * or with status {@code 500 (Internal Server Error)} if the authors couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/authors")
    public ResponseEntity<Authors> updateAuthors(@Valid @RequestBody Authors authors) throws URISyntaxException {
        log.debug("REST request to update Authors : {}", authors);
        if (authors.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Authors result = authorsService.save(authors);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, authors.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /authors} : get all the authors.
     *

     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of authors in body.
     */
    @GetMapping("/authors")
    public ResponseEntity<List<Authors>> getAllAuthors(Pageable pageable, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get a page of Authors");
        Page<Authors> page;
        if (eagerload) {
            page = authorsService.findAllWithEagerRelationships(pageable);
        } else {
            page = authorsService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /authors/:id} : get the "id" authors.
     *
     * @param id the id of the authors to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authors, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/authors/{id}")
    public ResponseEntity<Authors> getAuthors(@PathVariable Long id) {
        log.debug("REST request to get Authors : {}", id);
        Optional<Authors> authors = authorsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(authors);
    }

    /**
     * {@code DELETE  /authors/:id} : delete the "id" authors.
     *
     * @param id the id of the authors to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteAuthors(@PathVariable Long id) {
        log.debug("REST request to delete Authors : {}", id);
        authorsService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/authors?query=:query} : search for the authors corresponding
     * to the query.
     *
     * @param query the query of the authors search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/authors")
    public ResponseEntity<List<Authors>> searchAuthors(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Authors for query {}", query);
        Page<Authors> page = authorsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
