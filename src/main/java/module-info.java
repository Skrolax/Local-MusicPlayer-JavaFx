module com.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires javafx.media;

    opens com.musicplayer to javafx.fxml;
    exports com.musicplayer;
}