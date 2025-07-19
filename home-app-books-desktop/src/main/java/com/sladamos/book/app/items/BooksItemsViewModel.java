package com.sladamos.book.app.items;

import com.sladamos.book.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Getter
@Slf4j
@Component
@RequiredArgsConstructor
public class BooksItemsViewModel {

    private final ObservableList<BookItemViewModel> books = FXCollections.observableArrayList();

    public void loadBooks(List<Book> allBooks) {
        books.clear();
        allBooks.stream()
                .map(this::toViewModel)
                .forEach(books::add);
    }

    public void addBook(Book book) {
        books.add(toViewModel(book));
    }

    public void updateBook(Book book) {
        books.stream()
                .filter(vm -> vm.getId().get().equals(book.getId()))
                .findFirst()
                .ifPresentOrElse(
                        vm -> vm.updateFrom(book),
                        () -> books.add(toViewModel(book))
                );
    }

    public void deleteBook(UUID bookId) {
        books.removeIf(e -> e.getId().get().equals(bookId));
    }

    private BookItemViewModel toViewModel(Book book) {
        return new BookItemViewModel(book);
    }
}
