package Control;

import Commandline.DictionaryCommandLine;
import Commandline.Main;
import Database.DatabaseController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import com.jfoenix.controls.JFXButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DictionaryController extends DatabaseController  implements Initializable {

    private  Stage stage;
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

    @FXML
    private  JFXButton myWordButton;
    DictionaryCommandLine dictionaryCommandLine = DictionaryCommandLine.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectdataBase();
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

        myWordButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                switchComponent("./src/main/resources/Commandline/MyWordGui.fxml");
            }
        });

        googleTranslateButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                switchComponent("./src/main/resources/Commandline/GoogleTranslateGui.fxml");
            }
        });

        gameButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    switchtoGameMenu();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        ExitIcon.setOnMouseClicked(event -> {
            dictionaryCommandLine.dictionaryExportToFile();
            System.exit(0);
        });


        miniIcon.setOnMouseClicked(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Thu nhỏ màn hình
            stage.setIconified(true);
        });
    }

    private void setNode(Node node) {
        paneSwitch.getChildren().clear();
        paneSwitch.getChildren().add(node);
    }

    @FXML
    protected void switchComponent(String path) {
        try {
            URL url = new File(path).toURI().toURL();
            AnchorPane cmp = FXMLLoader.load(url);
            setNode(cmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchtoStudy() throws  IOException{
        URL url = new File("./src/main/resources/Commandline/StudyGui.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root,1030,679);
        Main.stageRefer.setScene(scene);
        Main.moveScreen(root,Main.stageRefer);
        Main.stageRefer.show();
    }

    public void switchtoDictionaryMain() throws  IOException {
        URL url = new File("./src/main/resources/Commandline/DictionaryGui.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root,1030,679);
        Main.stageRefer.setScene(scene);
        Main.moveScreen(root,Main.stageRefer);
        Main.stageRefer.show();
    }

    public void switchtoGameMenu() throws  IOException {
        URL url = new File("./src/main/resources/Commandline/MenuGame.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root,1030,679);
        Main.stageRefer.setScene(scene);
        Main.moveScreen(root,Main.stageRefer);
        Main.stageRefer.show();
    }

    public void switchtoGuessGame() throws  IOException {
        URL url = new File("./src/main/resources/Commandline/GuessWordGame.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root,1030,679);
        Main.stageRefer.setScene(scene);
        Main.moveScreen(root,Main.stageRefer);
        Main.stageRefer.show();
    }

    public void switchtoHangmanGame() throws  IOException{
        URL url = new File("./src/main/resources/Commandline/hangman.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root,900,645);
        Main.stageRefer.setScene(scene);
        Main.moveScreen(root,Main.stageRefer);
        Main.stageRefer.show();
    }
}
