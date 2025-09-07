package com.sladamos.book.app.items;

import com.sladamos.book.Book;
import jakarta.annotation.PostConstruct;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksItemsViewModel {

    private final ObservableList<BookItemViewModel> books = FXCollections.observableArrayList();

    private final FilteredList<BookItemViewModel> filteredBooks = new FilteredList<>(books, b -> true);

    @Getter
    private final SortedList<BookItemViewModel> sortedBooks = new SortedList<>(filteredBooks);

    @Getter
    private final StringProperty searchQuery = new SimpleStringProperty("");

    @Getter
    private final ObjectProperty<BooksItemsSortOption> sortOption = new SimpleObjectProperty<>(BooksItemsSortOption.TITLE_ASC);

    @PostConstruct
    public void init() {
        filteredBooks.predicateProperty().bind(
                Bindings.createObjectBinding(
                        () -> b -> searchQuery.get().isBlank()
                                || b.getTitle().get().toLowerCase().startsWith(searchQuery.get().toLowerCase()),
                        searchQuery
                )
        );

        sortedBooks.setComparator(sortOption.get().getComparator());
        sortOption.addListener((obs, oldVal, newVal) -> resort());
    }

    public void loadBooks(List<Book> allBooks) {
        books.clear();
        List<BookItemViewModel> booksVms = allBooks.stream()
                .map(this::toViewModel)
                .toList();
        books.addAll(booksVms);
    }

    public void addBook(Book book) {
        books.add(toViewModel(book));
        forceResort();
    }

    public void updateBook(Book book) {
        books.stream()
                .filter(vm -> vm.getId().get().equals(book.getId()))
                .findFirst()
                .ifPresent(vm -> vm.updateFrom(book));
        forceResort();
    }

    public void duplicateBook(Book book) {
        books.add(toViewModel(book));
        forceResort();
    }

    public void deleteBook(UUID bookId) {
        books.removeIf(e -> e.getId().get().equals(bookId));
    }

    public boolean areBooksNotLoaded() {
        return books.isEmpty();
    }

    private BookItemViewModel toViewModel(Book book) {
        return new BookItemViewModel(book);
    }

    private void forceResort() {
        log.info("Forcing resort of books");
        sortedBooks.setComparator(null);
        resort();
    }

    private void resort() {
        log.info("Resorting books: [sortOption: {}]", sortOption.get());
        sortedBooks.setComparator(sortOption.get().getComparator());
    }
}
