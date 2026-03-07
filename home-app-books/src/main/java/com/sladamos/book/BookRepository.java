package com.sladamos.book;

import com.sladamos.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    List<Book> findByAuthorsName(String authorName);
    List<Book> findByGenresName(String genreName);
}
