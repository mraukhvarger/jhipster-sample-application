package ru.raukh.micro.jhipstersample.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link GenresSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class GenresSearchRepositoryMockConfiguration {

    @MockBean
    private GenresSearchRepository mockGenresSearchRepository;

}
