package ru.raukh.micro.jhipstersample.service.impl;

import ru.raukh.micro.jhipstersample.service.BooksService;
import ru.raukh.micro.jhipstersample.domain.Books;
import ru.raukh.micro.jhipstersample.repository.BooksRepository;
import ru.raukh.micro.jhipstersample.repository.search.BooksSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Books}.
 */
@Service
@Transactional
public class BooksServiceImpl implements BooksService {

    private final Logger log = LoggerFactory.getLogger(BooksServiceImpl.class);

    private final BooksRepository booksRepository;

    private final BooksSearchRepository booksSearchRepository;

    public BooksServiceImpl(BooksRepository booksRepository, BooksSearchRepository booksSearchRepository) {
        this.booksRepository = booksRepository;
        this.booksSearchRepository = booksSearchRepository;
    }

    /**
     * Save a books.
     *
     * @param books the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Books save(Books books) {
        log.debug("Request to save Books : {}", books);
        Books result = booksRepository.save(books);
        booksSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the books.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Books> findAll(Pageable pageable) {
        log.debug("Request to get all Books");
        return booksRepository.findAll(pageable);
    }


    /**
     * Get one books by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Books> findOne(Long id) {
        log.debug("Request to get Books : {}", id);
        return booksRepository.findById(id);
    }

    /**
     * Delete the books by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Books : {}", id);
        booksRepository.deleteById(id);
        booksSearchRepository.deleteById(id);
    }

    /**
     * Search for the books corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Books> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Books for query {}", query);
        return booksSearchRepository.search(queryStringQuery(query), pageable);    }
}
