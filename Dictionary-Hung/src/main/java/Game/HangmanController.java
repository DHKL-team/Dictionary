package Game;

import Commandline.Word;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HangmanController extends Game implements Initializable {

    int hp;
    boolean isWin;
    String target = "";
    char[] guessedWord;

    @FXML
    AnchorPane pane;

    @FXML
    AnchorPane pane2;

    @FXML
    Label guessedLabel;

    @FXML
    GridPane alphabet;
    @FXML
    ImageView backMenu;

    @FXML
    ImageView exit;
    @FXML
    ImageView stage1;

    @FXML
    ImageView stage2;

    @FXML
    ImageView stage3;

    @FXML
    ImageView stage4;

    @FXML
    ImageView stage5;

    @FXML
    ImageView stage6;

    @FXML
    ImageView stage7;

    @FXML
    ImageView stage8;

    @FXML
    ImageView stage9;

    @FXML
    Label result;

    @FXML
    TextArea wordText;

    @FXML
    TextArea meanText;

    @FXML
    JFXButton again;

    public HangmanController() throws FileNotFoundException {
        hp = 7;
        isWin = false;
    }

    public void checkLetter(ActionEvent e) {
        boolean isCorrect = false;
        Button sourceButton = (Button)e.getSource();
        if (hp < 0 || sourceButton.getOpacity() == 0.4) return;
        sourceButton.setOpacity(0.4);
        String c = sourceButton.getText().toLowerCase();
        for (int i = 0; i < target.length(); i++) {
            if (target.charAt(i) == c.charAt(0)) {
                isCorrect = true;
                break;
            }
        }
        if (isCorrect) {
            for (int i = 0; i < target.length(); i++) {
                if (target.charAt(i) == c.charAt(0)) {
                    guessedWord[i] = c.charAt(0);
                }
            }
            guessedLabel.setText(new String(guessedWord).toUpperCase());
            for (char ca : guessedWord) {
                if (ca != '_') isWin = true;
                else {
                    isWin = false;
                    break;
                }
            }
            if (isWin) {
                pane2.setVisible(true);
                pane.setOpacity(0.4);
                result.setText("You win!");
            }
        } else {
            hp--;
            switch (hp) {
                case 6: stage2.setVisible(true); stage1.setVisible(false); break;
                case 5: stage3.setVisible(true); stage2.setVisible(false); break;
                case 4: stage4.setVisible(true); stage3.setVisible(false); break;
                case 3: stage5.setVisible(true); stage4.setVisible(false); break;
                case 2: stage6.setVisible(true); stage5.setVisible(false); break;
                case 1: stage7.setVisible(true); stage6.setVisible(false); break;
                case 0: stage8.setVisible(true); stage7.setVisible(false); break;
                case -1:
                    stage9.setVisible(true); stage8.setVisible(false);
                    result.setText("You lose :(");
                    pane.setOpacity(0.4);
                    pane2.setVisible(true);
                    break;
            }
        }
    }

    @FXML
    public void playAgain() {
        pane2.setVisible(false);
        pane.setOpacity(1);
        stage1.setVisible(true);
        stage2.setVisible(false);
        stage3.setVisible(false);
        stage4.setVisible(false);
        stage5.setVisible(false);
        stage6.setVisible(false);
        stage7.setVisible(false);
        stage8.setVisible(false);
        stage9.setVisible(false);
        for (Node node : alphabet.getChildren()) {
            if (node instanceof JFXButton) {
                JFXButton button = (JFXButton) node;
                button.setOpacity(1);
            }
        }
        hp = 7;
        isWin = false;
        Word word = randomWord();
        target = word.getWord_target();
        System.out.println(target);
        guessedWord = new char[target.length()];
        for (int i = 0; i < target.length(); i++) {
            guessedWord[i] = '_';
        }
        guessedLabel.setText(new String(guessedWord));
        wordText.setEditable(true);
        meanText.setEditable(true);
        wordText.setText(target + "\n" + word.getWord_pronunciation());
        meanText.setText(word.getWord_explain());
        wordText.setEditable(false);
        meanText.setEditable(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    switchtoGameMenu();
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
        playAgain();
        wordText.setStyle("-fx-control-inner-background:#FFCCCC;");
        meanText.setStyle("-fx-control-inner-background:#FFCCCC;");
    }

}