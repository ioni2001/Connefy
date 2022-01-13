module com.example.finalproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires java.sql;
    requires org.apache.pdfbox;
    //requires jdk.unsupported.desktop;

    opens com.example.finalproject to javafx.fxml, javafx.base, javafx.graphics;
    opens com.example.finalproject.domain;
    opens com.example.finalproject.repository;
    opens com.example.finalproject.service;
    exports com.example.finalproject;
}