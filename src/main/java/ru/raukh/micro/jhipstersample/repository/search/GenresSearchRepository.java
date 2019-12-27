package ru.raukh.micro.jhipstersample.repository.search;

import ru.raukh.micro.jhipstersample.domain.Genres;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Genres} entity.
 */
public interface GenresSearchRepository extends ElasticsearchRepository<Genres, Long> {
}
