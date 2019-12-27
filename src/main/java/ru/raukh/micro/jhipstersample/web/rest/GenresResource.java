package ru.raukh.micro.jhipstersample.web.rest;

import ru.raukh.micro.jhipstersample.domain.Genres;
import ru.raukh.micro.jhipstersample.service.GenresService;
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
 * REST controller for managing {@link ru.raukh.micro.jhipstersample.domain.Genres}.
 */
@RestController
@RequestMapping("/api")
public class GenresResource {

    private final Logger log = LoggerFactory.getLogger(GenresResource.class);

    private static final String ENTITY_NAME = "sampleServiceJHipsterGenres";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GenresService genresService;

    public GenresResource(GenresService genresService) {
        this.genresService = genresService;
    }

    /**
     * {@code POST  /genres} : Create a new genres.
     *
     * @param genres the genres to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new genres, or with status {@code 400 (Bad Request)} if the genres has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/genres")
    public ResponseEntity<Genres> createGenres(@Valid @RequestBody Genres genres) throws URISyntaxException {
        log.debug("REST request to save Genres : {}", genres);
        if (genres.getId() != null) {
            throw new BadRequestAlertException("A new genres cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Genres result = genresService.save(genres);
        return ResponseEntity.created(new URI("/api/genres/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /genres} : Updates an existing genres.
     *
     * @param genres the genres to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated genres,
     * or with status {@code 400 (Bad Request)} if the genres is not valid,
     * or with status {@code 500 (Internal Server Error)} if the genres couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/genres")
    public ResponseEntity<Genres> updateGenres(@Valid @RequestBody Genres genres) throws URISyntaxException {
        log.debug("REST request to update Genres : {}", genres);
        if (genres.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Genres result = genresService.save(genres);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, genres.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /genres} : get all the genres.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of genres in body.
     */
    @GetMapping("/genres")
    public ResponseEntity<List<Genres>> getAllGenres(Pageable pageable) {
        log.debug("REST request to get a page of Genres");
        Page<Genres> page = genresService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /genres/:id} : get the "id" genres.
     *
     * @param id the id of the genres to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the genres, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/genres/{id}")
    public ResponseEntity<Genres> getGenres(@PathVariable Long id) {
        log.debug("REST request to get Genres : {}", id);
        Optional<Genres> genres = genresService.findOne(id);
        return ResponseUtil.wrapOrNotFound(genres);
    }

    /**
     * {@code DELETE  /genres/:id} : delete the "id" genres.
     *
     * @param id the id of the genres to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/genres/{id}")
    public ResponseEntity<Void> deleteGenres(@PathVariable Long id) {
        log.debug("REST request to delete Genres : {}", id);
        genresService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/genres?query=:query} : search for the genres corresponding
     * to the query.
     *
     * @param query the query of the genres search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/genres")
    public ResponseEntity<List<Genres>> searchGenres(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Genres for query {}", query);
        Page<Genres> page = genresService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
