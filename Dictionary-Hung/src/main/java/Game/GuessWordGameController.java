package Game;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

import Commandline.Main;
import Database.DatabaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class GuessWordGameController extends Game implements Initializable {
    private static final int NUMBER_OF_LETTER = 7;
    private static final char[] nguyenAm = {'A', 'E', 'U', 'O', 'I'};
    private static int NUMBER_OF_LETTER_IN_WORD = 4;
    private static List<String> arrLetters = new ArrayList<>(NUMBER_OF_LETTER);
    private static List<String> resultArray = new ArrayList<>(5);
    Stack<Label> st = new Stack<>();
    private String tmp = "";
    private String str = "";
    private int col = 0;
    private int row = 0;
    private int countLeft = 10;
    private String source;
    private String lv = "Choose Level";
    @FXML
    private Label arr1;
    @FXML
    private Label arr2;
    @FXML
    private Label arr3;
    @FXML
    private Label arr4;
    @FXML
    private Label arr5;
    @FXML
    private Label arr6;
    @FXML
    private Label arr7;
    @FXML
    private GridPane gridPane;
    @FXML
    private Button quaylai;
    @FXML
    private Label attempleft;
    @FXML
    private Button replay;
    @FXML
    private ImageView exit;
    @FXML
    private ImageView backtogamemenu;
    @FXML
    private Button gotolevel;
    @FXML
    private ChoiceBox<String> Level;

    public GuessWordGameController() throws FileNotFoundException {
    }

    public static void makeRandomLetters() {
        if (arrLetters.isEmpty()) {
            arrLetters.add("H");
            arrLetters.add("T");
            arrLetters.add("O");
            arrLetters.add("I");
            arrLetters.add("U");
            arrLetters.add("A");
            arrLetters.add("E");
        }

        for (int i = 0; i < 7; i++) {
            char c;
            String s = "";
            if (i < 5) {
                do {
                    c = (char) ('A' + Math.floor(Math.random() * 26));
                    s = String.valueOf(c);
                }
                while (arrLetters.contains(s));
            } else {
                do {
                    int x = (int) Math.floor(Math.random() * 4);
                    c = nguyenAm[x];
                    s = String.valueOf(c);
                }
                while (arrLetters.contains(s));
            }
            arrLetters.set(i, s);
        }
    }

    @FXML
    private void switchComponentLevel(String path) throws IOException {
        URL url = new File(path).toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root, 1030, 679);
        Main.stageRefer.setScene(scene);
        Main.moveScreen(root, Main.stageRefer);
        Main.stageRefer.show();
    }

    @FXML
    public void GoToLevelFunc() throws IOException {
        switchComponentLevel(source);
    }

    @FXML
    public void BackToGameMenuFunc() throws IOException {
        switchToGameMenu();
    }

    @FXML
    public void eventMouseClick1() throws SQLException {
        tmp = arr1.getText();
        addWord();
    }

    @FXML
    public void eventMouseClick2() throws SQLException {
        tmp = arr2.getText();
        addWord();
    }

    @FXML
    public void eventMouseClick3() throws SQLException {
        tmp = arr3.getText();
        addWord();
    }

    @FXML
    public void eventMouseClick4() throws SQLException {
        tmp = arr4.getText();
        addWord();
    }

    @FXML
    public void eventMouseClick5() throws SQLException {
        tmp = arr5.getText();
        addWord();
    }

    @FXML
    public void eventMouseClick6() throws SQLException {
        tmp = arr6.getText();
        addWord();
    }

    @FXML
    public void eventMouseClick7() throws SQLException {
        tmp = arr7.getText();
        addWord();
    }

    @FXML
    public void Undo() {
        if (!st.isEmpty() && str.length() > 0) {
            gridPane.getChildren().remove(st.pop());
            str = str.substring(0, str.length() - 1);
            col--;
        }

    }

    @FXML
    public void replayFunction() throws IOException {
        switchComponentLevel("./src/main/resources/Commandline/GuessWordGame.fxml");
    }

    public void setLevel(String s) {
        if (s.equals("Khó")) {
            source = "./src/main/resources/Commandline/GuessWordGame5.fxml";
            lv = "Khó";
            NUMBER_OF_LETTER_IN_WORD = 5;
        } else if (s.equals("Trung bình")) {
            source = "./src/main/resources/Commandline/GuessWordGame.fxml";
            lv = "Trung bình";
            NUMBER_OF_LETTER_IN_WORD = 4;
        } else if (s.equals("Dễ")) {
            source = "./src/main/resources/Commandline/GuessWordGame3.fxml";
            lv = "Dễ";
            NUMBER_OF_LETTER_IN_WORD = 3;
        }
        resultArray.clear();
    }

    public void addWord() throws SQLException {
        Label lb = new Label(tmp);
        lb.getStyleClass().add("guessGameGridPane");
        gridPane.add(lb, col, row);
        gridPane.setHalignment(lb, javafx.geometry.HPos.CENTER);
        gridPane.setValignment(lb, javafx.geometry.VPos.CENTER);
        st.push(lb);
        col++;
        str += tmp;
        if (str.length() == NUMBER_OF_LETTER_IN_WORD) {
            Statement state = connection.createStatement();
            ResultSet result = state.executeQuery("select definition from wordlist where english = " + "'" + str + "'");
            if (result.next()) {
                row++;
                resultArray.add(str);
            } else {
                for (int i = 0; i < NUMBER_OF_LETTER_IN_WORD; i++) gridPane.getChildren().remove(st.pop());
                countLeft--;
            }
            str = "";
            col = 0;
            attempleft.setText(String.valueOf(countLeft));
        }
        if (countLeft > 0) {
            if (resultArray.size() == 5) showWinningAlert();
        } else showLosingAlert();
    }

    private void showWinningAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Congratulation!");
        alert.setHeaderText(null);
        alert.setContentText("Chúc mừng! Bạn đã chiến thắng!");
        alert.showAndWait();
    }

    private void showLosingAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Try Harder!");
        alert.setHeaderText(null);
        alert.setContentText("Hãy cố gắng ở lần thử tiếp theo!");
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectdataBase();
        backtogamemenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    BackToGameMenuFunc();
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
        makeRandomLetters();
        arr1.setText(arrLetters.get(0));
        arr2.setText(arrLetters.get(1));
        arr3.setText(arrLetters.get(2));
        arr4.setText(arrLetters.get(3));
        arr5.setText(arrLetters.get(4));
        arr6.setText(arrLetters.get(5));
        arr7.setText(arrLetters.get(6));
        attempleft.setText(String.valueOf(countLeft));

        ObservableList<String> choice1 = FXCollections.observableArrayList("Khó", "Trung bình", "Dễ");
        Level.setItems(choice1);
        Level.setValue(lv);
        Level.setOnAction(e -> setLevel(Level.getValue()));
    }
}