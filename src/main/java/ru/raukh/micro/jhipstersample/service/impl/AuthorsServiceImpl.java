package ru.raukh.micro.jhipstersample.service.impl;

import ru.raukh.micro.jhipstersample.service.AuthorsService;
import ru.raukh.micro.jhipstersample.domain.Authors;
import ru.raukh.micro.jhipstersample.repository.AuthorsRepository;
import ru.raukh.micro.jhipstersample.repository.search.AuthorsSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Authors}.
 */
@Service
@Transactional
public class AuthorsServiceImpl implements AuthorsService {

    private final Logger log = LoggerFactory.getLogger(AuthorsServiceImpl.class);

    private final AuthorsRepository authorsRepository;

    private final AuthorsSearchRepository authorsSearchRepository;

    public AuthorsServiceImpl(AuthorsRepository authorsRepository, AuthorsSearchRepository authorsSearchRepository) {
        this.authorsRepository = authorsRepository;
        this.authorsSearchRepository = authorsSearchRepository;
    }

    /**
     * Save a authors.
     *
     * @param authors the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Authors save(Authors authors) {
        log.debug("Request to save Authors : {}", authors);
        Authors result = authorsRepository.save(authors);
        authorsSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the authors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Authors> findAll(Pageable pageable) {
        log.debug("Request to get all Authors");
        return authorsRepository.findAll(pageable);
    }

    /**
     * Get all the authors with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Authors> findAllWithEagerRelationships(Pageable pageable) {
        return authorsRepository.findAllWithEagerRelationships(pageable);
    }
    

    /**
     * Get one authors by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Authors> findOne(Long id) {
        log.debug("Request to get Authors : {}", id);
        return authorsRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the authors by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Authors : {}", id);
        authorsRepository.deleteById(id);
        authorsSearchRepository.deleteById(id);
    }

    /**
     * Search for the authors corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Authors> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Authors for query {}", query);
        return authorsSearchRepository.search(queryStringQuery(query), pageable);    }
}
