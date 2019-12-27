package ru.raukh.micro.jhipstersample.repository.search;

import ru.raukh.micro.jhipstersample.domain.Authors;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Authors} entity.
 */
public interface AuthorsSearchRepository extends ElasticsearchRepository<Authors, Long> {
}
