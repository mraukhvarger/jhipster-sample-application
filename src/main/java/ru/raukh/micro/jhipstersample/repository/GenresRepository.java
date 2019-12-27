package ru.raukh.micro.jhipstersample.repository;

import ru.raukh.micro.jhipstersample.domain.Genres;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Genres entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GenresRepository extends JpaRepository<Genres, Long> {

}
