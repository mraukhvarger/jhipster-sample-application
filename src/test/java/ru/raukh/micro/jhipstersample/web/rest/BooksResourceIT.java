package ru.raukh.micro.jhipstersample.web.rest;

import ru.raukh.micro.jhipstersample.SampleServiceJHipsterApp;
import ru.raukh.micro.jhipstersample.domain.Books;
import ru.raukh.micro.jhipstersample.repository.BooksRepository;
import ru.raukh.micro.jhipstersample.repository.search.BooksSearchRepository;
import ru.raukh.micro.jhipstersample.service.BooksService;
import ru.raukh.micro.jhipstersample.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static ru.raukh.micro.jhipstersample.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BooksResource} REST controller.
 */
@SpringBootTest(classes = SampleServiceJHipsterApp.class)
public class BooksResourceIT {

    private static final String DEFAULT_BOOK_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BOOK_NAME = "BBBBBBBBBB";

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private BooksService booksService;

    /**
     * This repository is mocked in the ru.raukh.micro.jhipstersample.repository.search test package.
     *
     * @see ru.raukh.micro.jhipstersample.repository.search.BooksSearchRepositoryMockConfiguration
     */
    @Autowired
    private BooksSearchRepository mockBooksSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restBooksMockMvc;

    private Books books;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BooksResource booksResource = new BooksResource(booksService);
        this.restBooksMockMvc = MockMvcBuilders.standaloneSetup(booksResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Books createEntity(EntityManager em) {
        Books books = new Books()
            .bookName(DEFAULT_BOOK_NAME);
        return books;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Books createUpdatedEntity(EntityManager em) {
        Books books = new Books()
            .bookName(UPDATED_BOOK_NAME);
        return books;
    }

    @BeforeEach
    public void initTest() {
        books = createEntity(em);
    }

    @Test
    @Transactional
    public void createBooks() throws Exception {
        int databaseSizeBeforeCreate = booksRepository.findAll().size();

        // Create the Books
        restBooksMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(books)))
            .andExpect(status().isCreated());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeCreate + 1);
        Books testBooks = booksList.get(booksList.size() - 1);
        assertThat(testBooks.getBookName()).isEqualTo(DEFAULT_BOOK_NAME);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(1)).save(testBooks);
    }

    @Test
    @Transactional
    public void createBooksWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = booksRepository.findAll().size();

        // Create the Books with an existing ID
        books.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBooksMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(books)))
            .andExpect(status().isBadRequest());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeCreate);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(0)).save(books);
    }


    @Test
    @Transactional
    public void checkBookNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = booksRepository.findAll().size();
        // set the field null
        books.setBookName(null);

        // Create the Books, which fails.

        restBooksMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(books)))
            .andExpect(status().isBadRequest());

        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBooks() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get all the booksList
        restBooksMockMvc.perform(get("/api/books?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(books.getId().intValue())))
            .andExpect(jsonPath("$.[*].bookName").value(hasItem(DEFAULT_BOOK_NAME)));
    }
    
    @Test
    @Transactional
    public void getBooks() throws Exception {
        // Initialize the database
        booksRepository.saveAndFlush(books);

        // Get the books
        restBooksMockMvc.perform(get("/api/books/{id}", books.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(books.getId().intValue()))
            .andExpect(jsonPath("$.bookName").value(DEFAULT_BOOK_NAME));
    }

    @Test
    @Transactional
    public void getNonExistingBooks() throws Exception {
        // Get the books
        restBooksMockMvc.perform(get("/api/books/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBooks() throws Exception {
        // Initialize the database
        booksService.save(books);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockBooksSearchRepository);

        int databaseSizeBeforeUpdate = booksRepository.findAll().size();

        // Update the books
        Books updatedBooks = booksRepository.findById(books.getId()).get();
        // Disconnect from session so that the updates on updatedBooks are not directly saved in db
        em.detach(updatedBooks);
        updatedBooks
            .bookName(UPDATED_BOOK_NAME);

        restBooksMockMvc.perform(put("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBooks)))
            .andExpect(status().isOk());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
        Books testBooks = booksList.get(booksList.size() - 1);
        assertThat(testBooks.getBookName()).isEqualTo(UPDATED_BOOK_NAME);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(1)).save(testBooks);
    }

    @Test
    @Transactional
    public void updateNonExistingBooks() throws Exception {
        int databaseSizeBeforeUpdate = booksRepository.findAll().size();

        // Create the Books

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBooksMockMvc.perform(put("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(books)))
            .andExpect(status().isBadRequest());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(0)).save(books);
    }

    @Test
    @Transactional
    public void deleteBooks() throws Exception {
        // Initialize the database
        booksService.save(books);

        int databaseSizeBeforeDelete = booksRepository.findAll().size();

        // Delete the books
        restBooksMockMvc.perform(delete("/api/books/{id}", books.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(1)).deleteById(books.getId());
    }

    @Test
    @Transactional
    public void searchBooks() throws Exception {
        // Initialize the database
        booksService.save(books);
        when(mockBooksSearchRepository.search(queryStringQuery("id:" + books.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(books), PageRequest.of(0, 1), 1));
        // Search the books
        restBooksMockMvc.perform(get("/api/_search/books?query=id:" + books.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(books.getId().intValue())))
            .andExpect(jsonPath("$.[*].bookName").value(hasItem(DEFAULT_BOOK_NAME)));
    }
}
