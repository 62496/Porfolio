module app.controller{
    requires javafx.controls;
    requires javafx.fxml;
    requires tess4j;
    requires org.slf4j;
    requires java.sql;

    exports app;
    exports app.controller;
    opens app to javafx.fxml;
    opens app.controller to javafx.fxml;
    exports app.model;
    opens app.model to javafx.fxml;
}
