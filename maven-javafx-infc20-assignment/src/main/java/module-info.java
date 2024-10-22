module se.ics.lu {
    exports se.ics.lu;

    opens se.ics.lu.controllers to javafx.fxml;
    opens se.ics.lu.models to javafx.base;

    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.base;
}
