package ru.raukh.micro.jhipstersample.repository;

import ru.raukh.micro.jhipstersample.domain.Authors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Authors entity.
 */
@Repository
public interface AuthorsRepository extends JpaRepository<Authors, Long> {

    @Query(value = "select distinct authors from Authors authors left join fetch authors.books",
        countQuery = "select count(distinct authors) from Authors authors")
    Page<Authors> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct authors from Authors authors left join fetch authors.books")
    List<Authors> findAllWithEagerRelationships();

    @Query("select authors from Authors authors left join fetch authors.books where authors.id =:id")
    Optional<Authors> findOneWithEagerRelationships(@Param("id") Long id);

}
