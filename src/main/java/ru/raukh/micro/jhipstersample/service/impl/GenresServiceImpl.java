package ru.raukh.micro.jhipstersample.service.impl;

import ru.raukh.micro.jhipstersample.service.GenresService;
import ru.raukh.micro.jhipstersample.domain.Genres;
import ru.raukh.micro.jhipstersample.repository.GenresRepository;
import ru.raukh.micro.jhipstersample.repository.search.GenresSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Genres}.
 */
@Service
@Transactional
public class GenresServiceImpl implements GenresService {

    private final Logger log = LoggerFactory.getLogger(GenresServiceImpl.class);

    private final GenresRepository genresRepository;

    private final GenresSearchRepository genresSearchRepository;

    public GenresServiceImpl(GenresRepository genresRepository, GenresSearchRepository genresSearchRepository) {
        this.genresRepository = genresRepository;
        this.genresSearchRepository = genresSearchRepository;
    }

    /**
     * Save a genres.
     *
     * @param genres the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Genres save(Genres genres) {
        log.debug("Request to save Genres : {}", genres);
        Genres result = genresRepository.save(genres);
        genresSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the genres.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Genres> findAll(Pageable pageable) {
        log.debug("Request to get all Genres");
        return genresRepository.findAll(pageable);
    }


    /**
     * Get one genres by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Genres> findOne(Long id) {
        log.debug("Request to get Genres : {}", id);
        return genresRepository.findById(id);
    }

    /**
     * Delete the genres by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Genres : {}", id);
        genresRepository.deleteById(id);
        genresSearchRepository.deleteById(id);
    }

    /**
     * Search for the genres corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Genres> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Genres for query {}", query);
        return genresSearchRepository.search(queryStringQuery(query), pageable);    }
}
