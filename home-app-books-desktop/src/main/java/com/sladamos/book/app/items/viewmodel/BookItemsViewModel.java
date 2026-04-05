package com.sladamos.book.app.items.viewmodel;

import com.sladamos.book.app.items.BookCacheService;
import com.sladamos.book.app.items.BookItemsSortOption;
import com.sladamos.book.model.Book;
import jakarta.annotation.PostConstruct;
import javafx.beans.Observable;
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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class BookItemsViewModel {

    private final ObjectProvider<BookItemViewModel> viewModelProvider;
    private final BookItemsActiveState activeState;
    private final BookCacheService bookCacheService;

    @Getter
    private final ObservableList<BookItemViewModel> books = FXCollections.observableArrayList(
            vm -> new Observable[]{
                    vm.getTitle(), vm.getModificationDate(), vm.getCreationDate(), vm.getPages()
            }
    );

    private final FilteredList<BookItemViewModel> filteredBooks = new FilteredList<>(books, b -> true);

    @Getter
    private final SortedList<BookItemViewModel> sortedBooks = new SortedList<>(filteredBooks);

    @Getter
    private final StringProperty searchQuery = new SimpleStringProperty("");

    @Getter
    private final ObjectProperty<BookItemsSortOption> sortOption = new SimpleObjectProperty<>(BookItemsSortOption.MODIFICATION_DATE_DESC);

    @PostConstruct
    public void init() {
        filteredBooks.predicateProperty().bind(
                Bindings.createObjectBinding(() -> b -> searchQuery.get().isBlank() ||
                        b.getTitle().get().toLowerCase().startsWith(searchQuery.get().toLowerCase()), searchQuery)
        );

        sortedBooks.setComparator(sortOption.get().getComparator());
        sortOption.addListener((obs, oldVal, newVal) -> resort());

        activeState.register(this);
        getBooksFromCache();
    }

    public void addBook(Book book) {
        books.add(toViewModel(book));
        searchQuery.setValue("");
    }

    public void updateBook(Book book) {
        books.stream()
                .filter(vm -> vm.getId().get().equals(book.getId()))
                .findFirst()
                .ifPresent(vm -> vm.updateFrom(book));
    }

    public void deleteBook(UUID bookId) {
        books.removeIf(e -> e.getId().get().equals(bookId));
    }

    private void getBooksFromCache() {
        List<Book> books = bookCacheService.getBooks();
        List<BookItemViewModel> booksVm = books.stream().map(this::toViewModel).toList();
        this.books.addAll(booksVm);
    }

    private BookItemViewModel toViewModel(Book book) {
        BookItemViewModel vm = viewModelProvider.getObject();
        vm.init(book);
        return vm;
    }

    private void resort() {
        log.info("Resorting books: [sortOption: {}]", sortOption.get());
        sortedBooks.setComparator(sortOption.get().getComparator());
    }
}
