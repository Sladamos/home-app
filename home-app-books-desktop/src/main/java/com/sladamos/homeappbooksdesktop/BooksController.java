package com.sladamos.homeappbooksdesktop;

import com.sladamos.book.BookService;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BooksController {

    @FXML
    private VBox booksContainer;

    private final BookService bookService;

    @FXML
    public void initialize() {
        var books = bookService.getAllBooks();
        System.out.println(books.size());
    }
}
