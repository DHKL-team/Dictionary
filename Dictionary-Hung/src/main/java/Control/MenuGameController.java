package Control;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class MenuGameController extends DictionaryController implements Initializable  {

    @FXML
    private Button Hangman;

    @FXML
    private Button GuessWord;

    @FXML
    private ImageView exit;

    @FXML
    private ImageView backtomenu;

    @FXML
    public void HangmanFunc() throws IOException {
        switchtoHangmanGame();
    }

    @FXML
    public void GuessWordFunc() throws IOException {
        switchtoGuessGame();
    }

    public void ExitFunc() {
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
         backtomenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
             @Override
             public void handle(MouseEvent event) {
                 try {
                     switchtoDictionaryMain();
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
             }
         });

         exit.setOnMouseClicked(new EventHandler<MouseEvent>() {
             @Override
             public void handle(MouseEvent event) {
                 ExitFunc();
             }
         });
    }


}
