package com.sladamos.book.repository;

import com.sladamos.book.model.BookEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"authors", "genres"})
    List<BookEntity> findAll();

    List<BookEntity> findByAuthorsName(String authorName);
    List<BookEntity> findByGenresName(String genreName);
}
