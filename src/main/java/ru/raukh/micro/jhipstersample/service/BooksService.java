package ru.raukh.micro.jhipstersample.service;

import ru.raukh.micro.jhipstersample.domain.Books;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Books}.
 */
public interface BooksService {

    /**
     * Save a books.
     *
     * @param books the entity to save.
     * @return the persisted entity.
     */
    Books save(Books books);

    /**
     * Get all the books.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Books> findAll(Pageable pageable);


    /**
     * Get the "id" books.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Books> findOne(Long id);

    /**
     * Delete the "id" books.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the books corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Books> search(String query, Pageable pageable);
}
