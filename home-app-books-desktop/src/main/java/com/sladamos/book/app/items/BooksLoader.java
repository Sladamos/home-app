package com.sladamos.book.app.items;

import com.sladamos.book.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksLoader {

    private final BooksItemsViewModel viewModel;

    private final BookService bookService;

    public void loadBooks() {
        log.info("Loading books from service");
        viewModel.loadBooks(bookService.getAllBooks());
    }
}
