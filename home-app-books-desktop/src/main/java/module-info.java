module home.app.books.desktop {
    requires home.app.books;
    requires home.app.common.desktop;
    requires jakarta.annotation;
    requires jakarta.validation;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires static lombok;
    requires micrometer.commons;
    requires spring.context;
    requires spring.core;
    requires spring.tx;
    requires org.slf4j;

    opens com.sladamos.book.app;
    opens com.sladamos.book.app.util;
    opens com.sladamos.book.app.add;
    opens com.sladamos.book.app.edit;
    opens com.sladamos.book.app.items;
    opens com.sladamos.book.app.modify;
    opens com.sladamos.book.app.modify.components;
    opens com.sladamos.book.app.modify.validation;
    exports com.sladamos.book.app;
}