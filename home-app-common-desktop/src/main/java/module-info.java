module home.app.common.desktop {
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires static lombok;
    requires spring.beans;
    requires spring.context;

    opens com.sladamos.app.util;
    opens com.sladamos.app.util.messages;
    opens com.sladamos.app.util.components;

    exports com.sladamos.app.util;
    exports com.sladamos.app.util.messages;
    exports com.sladamos.app.util.components;
}