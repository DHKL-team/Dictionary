module Commandline {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.jfoenix;

    opens Commandline to javafx.fxml;
    exports Commandline;
    exports Control;
    opens Control to javafx.fxml;
}