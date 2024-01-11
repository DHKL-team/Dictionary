module Commandline {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.jfoenix;
    requires javafx.web;
    requires javafx.media;
    requires java.sql;
    requires java.speech.api;
    requires freetts;
	requires java.desktop;
	requires javaFlacEncoder;


    opens Commandline to javafx.fxml;
    exports Commandline;
    exports Control;
    opens Control to javafx.fxml;
    opens  Game to javafx.fxml;
    exports Game;
}