package ru.raukh.micro.jhipstersample.service;

import ru.raukh.micro.jhipstersample.domain.Genres;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Genres}.
 */
public interface GenresService {

    /**
     * Save a genres.
     *
     * @param genres the entity to save.
     * @return the persisted entity.
     */
    Genres save(Genres genres);

    /**
     * Get all the genres.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Genres> findAll(Pageable pageable);


    /**
     * Get the "id" genres.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Genres> findOne(Long id);

    /**
     * Delete the "id" genres.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the genres corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Genres> search(String query, Pageable pageable);
}
