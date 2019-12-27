package ru.raukh.micro.jhipstersample.service;

import ru.raukh.micro.jhipstersample.domain.Authors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Authors}.
 */
public interface AuthorsService {

    /**
     * Save a authors.
     *
     * @param authors the entity to save.
     * @return the persisted entity.
     */
    Authors save(Authors authors);

    /**
     * Get all the authors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Authors> findAll(Pageable pageable);

    /**
     * Get all the authors with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Page<Authors> findAllWithEagerRelationships(Pageable pageable);
    
    /**
     * Get the "id" authors.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Authors> findOne(Long id);

    /**
     * Delete the "id" authors.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the authors corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Authors> search(String query, Pageable pageable);
}
