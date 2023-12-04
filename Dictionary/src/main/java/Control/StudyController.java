package Control;

import Database.DatabaseController;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class StudyController extends DatabaseController implements Initializable {
    @FXML
    public GridPane gridPane;
    @FXML
    private ImageView backButton;

    @FXML
    private StackPane stackPane;

    @FXML
    private Label questionLabel;
    private ObservableList<String> list;
    private ObservableList<String> listDef;
    private int CURRENT_ROW = 1;
    private int CURRENT_COLUMN = 1;

    private int MAX_COLUMN;
    private String chooseWord;

    private String chooseWordDef;
    private int selectWordToStudy;
    @FXML
    private ImageView correctLabel;

    @FXML
    private ImageView wrongLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectdataBase();
        list = mywordList();
        listDef = mydefList();
        correctLabel.setVisible(false);
        wrongLabel.setVisible(false);
        selectWordToStudy = 0;
        chooseWord = list.get(selectWordToStudy);
        chooseWordDef = listDef.get(selectWordToStudy);
        questionLabel.setText(chooseWordDef);
        MAX_COLUMN = chooseWord.length();
        gridPane = createGrid(MAX_COLUMN);
        gridPane.setFocusTraversable(true);
        gridPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                onKeyPress(gridPane, keyEvent);
            }
        });
        stackPane.getChildren().add(gridPane);
        backButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    new DictionaryController().switchtoDictionaryMain();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public GridPane createGrid(int col) {
        GridPane table = new GridPane();
        for (int i = 1; i <= col; i++) {
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.getStyleClass().add("default-tile");
            table.add(label, i, 1);
            GridPane.setMargin(label, new Insets(5));
        }
        table.setAlignment(Pos.CENTER);
        return table;
    }

    private Label getLabel(GridPane gridPane, int searchRow, int searchCol) {
        for (Node child : gridPane.getChildren()) {
            Integer r = GridPane.getRowIndex(child);
            Integer c = GridPane.getColumnIndex(child);
            int row = r == null ? 0 : r;
            int column = c == null ? 0 : c;
            if (row == searchRow && column == searchCol && (child instanceof Label))
                return (Label) child;
        }
        return null;
    }

    private String getLabelText(GridPane gridPane, int searchRow, int searchCol) {
        Label label = getLabel(gridPane, searchRow, searchCol);

        if (label != null) {
            return label.getText().toLowerCase();
        }
        return null;
    }

    public void onKeyPress(GridPane gridPane, KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            onEntterPressed(gridPane);
        }
        if (keyEvent.getCode().isLetterKey()) {
            onLetterPressed(gridPane, keyEvent);
        } else if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
            onBackSpacePredded(gridPane);
        }
    }

    private void onLetterPressed(GridPane gridPane, KeyEvent keyEvent) {
        if (Objects.equals(getLabelText(gridPane, CURRENT_ROW, CURRENT_COLUMN), "")) {
            setTextFieldText(gridPane, CURRENT_ROW, CURRENT_COLUMN, keyEvent.getText());
            Label label = getLabel(gridPane, CURRENT_ROW, CURRENT_COLUMN);
            ScaleTransition firstScaleTransition = new ScaleTransition(Duration.millis(100), label);
            firstScaleTransition.fromXProperty().setValue(1);
            firstScaleTransition.toXProperty().setValue(1.1);
            firstScaleTransition.fromYProperty().setValue(1);
            firstScaleTransition.toYProperty().setValue(1.1);
            ScaleTransition secondScaleTransition = new ScaleTransition(Duration.millis(100), label);
            secondScaleTransition.fromXProperty().setValue(1.1);
            secondScaleTransition.toXProperty().setValue(1);
            secondScaleTransition.fromYProperty().setValue(1.1);
            secondScaleTransition.toYProperty().setValue(1);
            new SequentialTransition(firstScaleTransition, secondScaleTransition).play();
            if (CURRENT_COLUMN < MAX_COLUMN) {
                CURRENT_COLUMN++;
            }
        }
    }

    private void onBackSpacePredded(GridPane gridPane) {
        if ((CURRENT_COLUMN == MAX_COLUMN || CURRENT_COLUMN == 1) && !Objects.equals(getLabelText(gridPane, CURRENT_ROW, CURRENT_COLUMN), "")) {
            setTextFieldText(gridPane, CURRENT_ROW, CURRENT_COLUMN, "");

        } else if ((CURRENT_COLUMN == MAX_COLUMN && Objects.equals(getLabelText(gridPane, CURRENT_ROW, CURRENT_COLUMN), ""))
                || (CURRENT_COLUMN > 1 && CURRENT_COLUMN < MAX_COLUMN)) {
            CURRENT_COLUMN--;
            setTextFieldText(gridPane, CURRENT_ROW, CURRENT_COLUMN, "");
        }
    }

    private void onEntterPressed(GridPane gridPane) {
        String word = readWordfromGridPane(gridPane).toLowerCase();
        if (word.length() == MAX_COLUMN) {
            if (word.equals(chooseWord)) {
                correctLabel.setVisible(true);
                wrongLabel.setVisible(false);
                PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1.7));
                pauseTransition.setOnFinished(event -> {
                    updateWordwasStudied(chooseWord);

                    selectWordToStudy++;
                    if (selectWordToStudy == list.size()) {
                        return;
                    }
                    chooseWord = list.get(selectWordToStudy);
                    chooseWordDef = listDef.get(selectWordToStudy);
                    questionLabel.setText(chooseWordDef);
                    resetWordCol();
                    correctLabel.setVisible(false);
                });
                pauseTransition.play();
            } else {
                wrongLabel.setVisible(true);
            }
        }

    }

    private String readWordfromGridPane(GridPane gridPane) {
        StringBuilder word = new StringBuilder();
        for (int i = 1; i <= MAX_COLUMN; i++) {
            word.append(getLabelText(gridPane, CURRENT_ROW, i));
        }
        return word.toString();
    }

    private void setTextFieldText(GridPane gridPane, int CURRENT_ROW, int currentCol, String text) {
        Label label = getLabel(gridPane, CURRENT_ROW, currentCol);
        if (label != null) {
            label.setText(text.toUpperCase());
        }
    }

    private void resetWordCol() {
        //checkAns.setVisible(false);
        MAX_COLUMN = chooseWord.length();
        gridPane = createGrid(MAX_COLUMN);
        stackPane.getChildren().remove(0);
        gridPane.setFocusTraversable(true);
        gridPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                onKeyPress(gridPane, keyEvent);
            }
        });
        stackPane.getChildren().add(gridPane);
        CURRENT_COLUMN = 1;
    }

    private void updateWordwasStudied(String s) {

        String sql = "UPDATE myword SET status = 1 WHERE english = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, s);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Word '" + s + "' status updated successfully.");
            } else {
                System.out.println("Word '" + s + "' not found in the database.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}



