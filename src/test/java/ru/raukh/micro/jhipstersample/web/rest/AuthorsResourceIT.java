package ru.raukh.micro.jhipstersample.web.rest;

import ru.raukh.micro.jhipstersample.SampleServiceJHipsterApp;
import ru.raukh.micro.jhipstersample.domain.Authors;
import ru.raukh.micro.jhipstersample.repository.AuthorsRepository;
import ru.raukh.micro.jhipstersample.repository.search.AuthorsSearchRepository;
import ru.raukh.micro.jhipstersample.service.AuthorsService;
import ru.raukh.micro.jhipstersample.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import java.util.ArrayList;
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
 * Integration tests for the {@link AuthorsResource} REST controller.
 */
@SpringBootTest(classes = SampleServiceJHipsterApp.class)
public class AuthorsResourceIT {

    private static final String DEFAULT_AUTHOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR_NAME = "BBBBBBBBBB";

    @Autowired
    private AuthorsRepository authorsRepository;

    @Mock
    private AuthorsRepository authorsRepositoryMock;

    @Mock
    private AuthorsService authorsServiceMock;

    @Autowired
    private AuthorsService authorsService;

    /**
     * This repository is mocked in the ru.raukh.micro.jhipstersample.repository.search test package.
     *
     * @see ru.raukh.micro.jhipstersample.repository.search.AuthorsSearchRepositoryMockConfiguration
     */
    @Autowired
    private AuthorsSearchRepository mockAuthorsSearchRepository;

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

    private MockMvc restAuthorsMockMvc;

    private Authors authors;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AuthorsResource authorsResource = new AuthorsResource(authorsService);
        this.restAuthorsMockMvc = MockMvcBuilders.standaloneSetup(authorsResource)
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
    public static Authors createEntity(EntityManager em) {
        Authors authors = new Authors()
            .authorName(DEFAULT_AUTHOR_NAME);
        return authors;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Authors createUpdatedEntity(EntityManager em) {
        Authors authors = new Authors()
            .authorName(UPDATED_AUTHOR_NAME);
        return authors;
    }

    @BeforeEach
    public void initTest() {
        authors = createEntity(em);
    }

    @Test
    @Transactional
    public void createAuthors() throws Exception {
        int databaseSizeBeforeCreate = authorsRepository.findAll().size();

        // Create the Authors
        restAuthorsMockMvc.perform(post("/api/authors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(authors)))
            .andExpect(status().isCreated());

        // Validate the Authors in the database
        List<Authors> authorsList = authorsRepository.findAll();
        assertThat(authorsList).hasSize(databaseSizeBeforeCreate + 1);
        Authors testAuthors = authorsList.get(authorsList.size() - 1);
        assertThat(testAuthors.getAuthorName()).isEqualTo(DEFAULT_AUTHOR_NAME);

        // Validate the Authors in Elasticsearch
        verify(mockAuthorsSearchRepository, times(1)).save(testAuthors);
    }

    @Test
    @Transactional
    public void createAuthorsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = authorsRepository.findAll().size();

        // Create the Authors with an existing ID
        authors.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuthorsMockMvc.perform(post("/api/authors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(authors)))
            .andExpect(status().isBadRequest());

        // Validate the Authors in the database
        List<Authors> authorsList = authorsRepository.findAll();
        assertThat(authorsList).hasSize(databaseSizeBeforeCreate);

        // Validate the Authors in Elasticsearch
        verify(mockAuthorsSearchRepository, times(0)).save(authors);
    }


    @Test
    @Transactional
    public void checkAuthorNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = authorsRepository.findAll().size();
        // set the field null
        authors.setAuthorName(null);

        // Create the Authors, which fails.

        restAuthorsMockMvc.perform(post("/api/authors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(authors)))
            .andExpect(status().isBadRequest());

        List<Authors> authorsList = authorsRepository.findAll();
        assertThat(authorsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAuthors() throws Exception {
        // Initialize the database
        authorsRepository.saveAndFlush(authors);

        // Get all the authorsList
        restAuthorsMockMvc.perform(get("/api/authors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(authors.getId().intValue())))
            .andExpect(jsonPath("$.[*].authorName").value(hasItem(DEFAULT_AUTHOR_NAME)));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllAuthorsWithEagerRelationshipsIsEnabled() throws Exception {
        AuthorsResource authorsResource = new AuthorsResource(authorsServiceMock);
        when(authorsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restAuthorsMockMvc = MockMvcBuilders.standaloneSetup(authorsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restAuthorsMockMvc.perform(get("/api/authors?eagerload=true"))
        .andExpect(status().isOk());

        verify(authorsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllAuthorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        AuthorsResource authorsResource = new AuthorsResource(authorsServiceMock);
            when(authorsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restAuthorsMockMvc = MockMvcBuilders.standaloneSetup(authorsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restAuthorsMockMvc.perform(get("/api/authors?eagerload=true"))
        .andExpect(status().isOk());

            verify(authorsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getAuthors() throws Exception {
        // Initialize the database
        authorsRepository.saveAndFlush(authors);

        // Get the authors
        restAuthorsMockMvc.perform(get("/api/authors/{id}", authors.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(authors.getId().intValue()))
            .andExpect(jsonPath("$.authorName").value(DEFAULT_AUTHOR_NAME));
    }

    @Test
    @Transactional
    public void getNonExistingAuthors() throws Exception {
        // Get the authors
        restAuthorsMockMvc.perform(get("/api/authors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAuthors() throws Exception {
        // Initialize the database
        authorsService.save(authors);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockAuthorsSearchRepository);

        int databaseSizeBeforeUpdate = authorsRepository.findAll().size();

        // Update the authors
        Authors updatedAuthors = authorsRepository.findById(authors.getId()).get();
        // Disconnect from session so that the updates on updatedAuthors are not directly saved in db
        em.detach(updatedAuthors);
        updatedAuthors
            .authorName(UPDATED_AUTHOR_NAME);

        restAuthorsMockMvc.perform(put("/api/authors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAuthors)))
            .andExpect(status().isOk());

        // Validate the Authors in the database
        List<Authors> authorsList = authorsRepository.findAll();
        assertThat(authorsList).hasSize(databaseSizeBeforeUpdate);
        Authors testAuthors = authorsList.get(authorsList.size() - 1);
        assertThat(testAuthors.getAuthorName()).isEqualTo(UPDATED_AUTHOR_NAME);

        // Validate the Authors in Elasticsearch
        verify(mockAuthorsSearchRepository, times(1)).save(testAuthors);
    }

    @Test
    @Transactional
    public void updateNonExistingAuthors() throws Exception {
        int databaseSizeBeforeUpdate = authorsRepository.findAll().size();

        // Create the Authors

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthorsMockMvc.perform(put("/api/authors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(authors)))
            .andExpect(status().isBadRequest());

        // Validate the Authors in the database
        List<Authors> authorsList = authorsRepository.findAll();
        assertThat(authorsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Authors in Elasticsearch
        verify(mockAuthorsSearchRepository, times(0)).save(authors);
    }

    @Test
    @Transactional
    public void deleteAuthors() throws Exception {
        // Initialize the database
        authorsService.save(authors);

        int databaseSizeBeforeDelete = authorsRepository.findAll().size();

        // Delete the authors
        restAuthorsMockMvc.perform(delete("/api/authors/{id}", authors.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Authors> authorsList = authorsRepository.findAll();
        assertThat(authorsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Authors in Elasticsearch
        verify(mockAuthorsSearchRepository, times(1)).deleteById(authors.getId());
    }

    @Test
    @Transactional
    public void searchAuthors() throws Exception {
        // Initialize the database
        authorsService.save(authors);
        when(mockAuthorsSearchRepository.search(queryStringQuery("id:" + authors.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(authors), PageRequest.of(0, 1), 1));
        // Search the authors
        restAuthorsMockMvc.perform(get("/api/_search/authors?query=id:" + authors.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(authors.getId().intValue())))
            .andExpect(jsonPath("$.[*].authorName").value(hasItem(DEFAULT_AUTHOR_NAME)));
    }
}
