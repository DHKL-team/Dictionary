package Control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import  com.jfoenix.controls.JFXButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DictionaryController implements Initializable {

    @FXML
    private ImageView ExitIcon;

    @FXML
    private JFXButton searchButton;
    @FXML
    private JFXButton addWordButton;

    @FXML
    private JFXButton gameButton;

    @FXML
    private JFXButton googleTranslateButton;

    @FXML
    private AnchorPane paneSwitch;

    @FXML
    private ImageView miniIcon;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
              @Override
              public void handle(MouseEvent mouseEvent) {
                  switchComponent("./src/main/resources/Commandline/SearchGui.fxml");
              }
          });

        addWordButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                switchComponent("./src/main/resources/Commandline/AddGui.fxml");
            }
        });
        ExitIcon.setOnMouseClicked(event -> {
            System.exit(0);
        });

        miniIcon.setOnMouseClicked(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Thu nhỏ màn hình
            stage.setIconified(true);
        });
    }
    private  void setNode(Node node){
        paneSwitch.getChildren().clear();
        paneSwitch.getChildren().add(node);
    }
    @FXML
    private void switchComponent(String path){
        try{

            URL url = new File(path).toURI().toURL();
            AnchorPane cmp = FXMLLoader.load(url);
            setNode(cmp);
        }catch (IOException e){
            e.printStackTrace();
        }
    }



}
