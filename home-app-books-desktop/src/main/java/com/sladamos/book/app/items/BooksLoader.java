package com.sladamos.book.app.items;

import com.sladamos.book.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksLoader {

    private final BooksItemsViewModel viewModel;

    private final BookService bookService;

    @Transactional
    public void loadBooks() {
        log.info("Loading books from service");
        viewModel.loadBooks(bookService.getAllBooks());
    }


}
