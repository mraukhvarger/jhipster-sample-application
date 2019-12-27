package ru.raukh.micro.jhipstersample.web.rest;

import ru.raukh.micro.jhipstersample.SampleServiceJHipsterApp;
import ru.raukh.micro.jhipstersample.domain.Genres;
import ru.raukh.micro.jhipstersample.repository.GenresRepository;
import ru.raukh.micro.jhipstersample.repository.search.GenresSearchRepository;
import ru.raukh.micro.jhipstersample.service.GenresService;
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
 * Integration tests for the {@link GenresResource} REST controller.
 */
@SpringBootTest(classes = SampleServiceJHipsterApp.class)
public class GenresResourceIT {

    private static final String DEFAULT_GENRE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_GENRE_NAME = "BBBBBBBBBB";

    @Autowired
    private GenresRepository genresRepository;

    @Autowired
    private GenresService genresService;

    /**
     * This repository is mocked in the ru.raukh.micro.jhipstersample.repository.search test package.
     *
     * @see ru.raukh.micro.jhipstersample.repository.search.GenresSearchRepositoryMockConfiguration
     */
    @Autowired
    private GenresSearchRepository mockGenresSearchRepository;

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

    private MockMvc restGenresMockMvc;

    private Genres genres;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final GenresResource genresResource = new GenresResource(genresService);
        this.restGenresMockMvc = MockMvcBuilders.standaloneSetup(genresResource)
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
    public static Genres createEntity(EntityManager em) {
        Genres genres = new Genres()
            .genreName(DEFAULT_GENRE_NAME);
        return genres;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Genres createUpdatedEntity(EntityManager em) {
        Genres genres = new Genres()
            .genreName(UPDATED_GENRE_NAME);
        return genres;
    }

    @BeforeEach
    public void initTest() {
        genres = createEntity(em);
    }

    @Test
    @Transactional
    public void createGenres() throws Exception {
        int databaseSizeBeforeCreate = genresRepository.findAll().size();

        // Create the Genres
        restGenresMockMvc.perform(post("/api/genres")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(genres)))
            .andExpect(status().isCreated());

        // Validate the Genres in the database
        List<Genres> genresList = genresRepository.findAll();
        assertThat(genresList).hasSize(databaseSizeBeforeCreate + 1);
        Genres testGenres = genresList.get(genresList.size() - 1);
        assertThat(testGenres.getGenreName()).isEqualTo(DEFAULT_GENRE_NAME);

        // Validate the Genres in Elasticsearch
        verify(mockGenresSearchRepository, times(1)).save(testGenres);
    }

    @Test
    @Transactional
    public void createGenresWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = genresRepository.findAll().size();

        // Create the Genres with an existing ID
        genres.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGenresMockMvc.perform(post("/api/genres")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(genres)))
            .andExpect(status().isBadRequest());

        // Validate the Genres in the database
        List<Genres> genresList = genresRepository.findAll();
        assertThat(genresList).hasSize(databaseSizeBeforeCreate);

        // Validate the Genres in Elasticsearch
        verify(mockGenresSearchRepository, times(0)).save(genres);
    }


    @Test
    @Transactional
    public void checkGenreNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = genresRepository.findAll().size();
        // set the field null
        genres.setGenreName(null);

        // Create the Genres, which fails.

        restGenresMockMvc.perform(post("/api/genres")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(genres)))
            .andExpect(status().isBadRequest());

        List<Genres> genresList = genresRepository.findAll();
        assertThat(genresList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllGenres() throws Exception {
        // Initialize the database
        genresRepository.saveAndFlush(genres);

        // Get all the genresList
        restGenresMockMvc.perform(get("/api/genres?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(genres.getId().intValue())))
            .andExpect(jsonPath("$.[*].genreName").value(hasItem(DEFAULT_GENRE_NAME)));
    }
    
    @Test
    @Transactional
    public void getGenres() throws Exception {
        // Initialize the database
        genresRepository.saveAndFlush(genres);

        // Get the genres
        restGenresMockMvc.perform(get("/api/genres/{id}", genres.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(genres.getId().intValue()))
            .andExpect(jsonPath("$.genreName").value(DEFAULT_GENRE_NAME));
    }

    @Test
    @Transactional
    public void getNonExistingGenres() throws Exception {
        // Get the genres
        restGenresMockMvc.perform(get("/api/genres/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGenres() throws Exception {
        // Initialize the database
        genresService.save(genres);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockGenresSearchRepository);

        int databaseSizeBeforeUpdate = genresRepository.findAll().size();

        // Update the genres
        Genres updatedGenres = genresRepository.findById(genres.getId()).get();
        // Disconnect from session so that the updates on updatedGenres are not directly saved in db
        em.detach(updatedGenres);
        updatedGenres
            .genreName(UPDATED_GENRE_NAME);

        restGenresMockMvc.perform(put("/api/genres")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedGenres)))
            .andExpect(status().isOk());

        // Validate the Genres in the database
        List<Genres> genresList = genresRepository.findAll();
        assertThat(genresList).hasSize(databaseSizeBeforeUpdate);
        Genres testGenres = genresList.get(genresList.size() - 1);
        assertThat(testGenres.getGenreName()).isEqualTo(UPDATED_GENRE_NAME);

        // Validate the Genres in Elasticsearch
        verify(mockGenresSearchRepository, times(1)).save(testGenres);
    }

    @Test
    @Transactional
    public void updateNonExistingGenres() throws Exception {
        int databaseSizeBeforeUpdate = genresRepository.findAll().size();

        // Create the Genres

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenresMockMvc.perform(put("/api/genres")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(genres)))
            .andExpect(status().isBadRequest());

        // Validate the Genres in the database
        List<Genres> genresList = genresRepository.findAll();
        assertThat(genresList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Genres in Elasticsearch
        verify(mockGenresSearchRepository, times(0)).save(genres);
    }

    @Test
    @Transactional
    public void deleteGenres() throws Exception {
        // Initialize the database
        genresService.save(genres);

        int databaseSizeBeforeDelete = genresRepository.findAll().size();

        // Delete the genres
        restGenresMockMvc.perform(delete("/api/genres/{id}", genres.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Genres> genresList = genresRepository.findAll();
        assertThat(genresList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Genres in Elasticsearch
        verify(mockGenresSearchRepository, times(1)).deleteById(genres.getId());
    }

    @Test
    @Transactional
    public void searchGenres() throws Exception {
        // Initialize the database
        genresService.save(genres);
        when(mockGenresSearchRepository.search(queryStringQuery("id:" + genres.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(genres), PageRequest.of(0, 1), 1));
        // Search the genres
        restGenresMockMvc.perform(get("/api/_search/genres?query=id:" + genres.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(genres.getId().intValue())))
            .andExpect(jsonPath("$.[*].genreName").value(hasItem(DEFAULT_GENRE_NAME)));
    }
}
