module home.app.books {
    requires jakarta.persistence;
    requires jakarta.validation;
    requires static lombok;
    requires micrometer.commons;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.data.jpa;
    requires spring.web;
    requires org.slf4j;
    requires org.hibernate.orm.core;

    opens com.sladamos;
    opens com.sladamos.book;
    opens com.sladamos.book.dto;
    opens com.sladamos.book.functions;
    opens com.sladamos.book.validators;

    exports com.sladamos.book;
}
