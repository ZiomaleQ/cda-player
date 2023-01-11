module tfo.dev.cdaplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.jsoup;
    requires com.google.gson;


    opens tfo.dev.cdaplayer to javafx.fxml;
    exports tfo.dev.cdaplayer;
}