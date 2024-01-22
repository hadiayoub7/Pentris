module com.tetris.welcome {
    requires javafx.controls;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    exports gui;
    exports ai;
    exports utils;
    exports pentominoes;
    opens gui to javafx.fxml;
}
