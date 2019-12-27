package ru.raukh.micro.jhipstersample.repository;

import ru.raukh.micro.jhipstersample.domain.Books;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Books entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BooksRepository extends JpaRepository<Books, Long> {

}
