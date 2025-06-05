package com.sladamos.book;

public class BookNotFoundException extends Throwable {
    public BookNotFoundException(String s) {
        super(s);
    }
}
