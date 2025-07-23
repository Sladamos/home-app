module home.app.desktop {
    requires home.app.books.desktop;
    requires home.app.common.desktop;
    requires javafx.fxml;
    requires javafx.graphics;
    requires static lombok;
    requires spring.context;

    opens com.sladamos.app;
}