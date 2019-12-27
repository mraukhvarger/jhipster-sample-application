package ru.raukh.micro.jhipstersample.repository.search;

import ru.raukh.micro.jhipstersample.domain.Books;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Books} entity.
 */
public interface BooksSearchRepository extends ElasticsearchRepository<Books, Long> {
}
