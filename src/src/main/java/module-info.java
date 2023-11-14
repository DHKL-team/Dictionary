module Commandline {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens Commandline to javafx.fxml;
    exports Commandline;
    exports Control;
    opens Control to javafx.fxml;
}