module com.example.finalproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires java.sql;
    //requires jdk.unsupported.desktop;

    opens com.example.finalproject to javafx.fxml;
    exports com.example.finalproject;
}