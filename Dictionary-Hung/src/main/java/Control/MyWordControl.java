package Control;

import Database.DatabaseController;
import com.jfoenix.controls.JFXButton;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class MyWordControl extends  DatabaseController implements Initializable {
    @FXML
    private ListView<String> mywordList;
    @FXML
    private ProgressBar percentwordStudy;
    @FXML
    private JFXButton studyButton;

    @FXML
    private ImageView removeWordButton;
    @FXML
    private ImageView closeButton;
    private ObservableList<String> list;
    private ObservableList<String> listdef;
    @FXML
    private Label numberofWord;

    @FXML
    private Label card;

    String selectWord;
    int idSelectWord;

    String selectDef;
    boolean isFrontShowing = true;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectdataBase();
        closeButton.setVisible(false);
        card.setVisible(false);
        removeWordButton.setVisible(false);
        list = mywordList();
        listdef = mydefList();
        mywordList.setItems(list);
        countWord();

        percentwordStudy.setProgress((double)numberOfWordwasStuddied()/list.size());
        card.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                RotateTransition rotator = createRotator(card);
                PauseTransition ptChangeCardFace = changeCardFace(card);

                ParallelTransition parallelTransition = new ParallelTransition(rotator,ptChangeCardFace);
                parallelTransition.play();
                isFrontShowing = !isFrontShowing;
            }
        });


        closeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                card.setVisible(false);
                closeButton.setVisible(false);
                removeWordButton.setVisible(false);
            }
        });

        studyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    new DictionaryController().switchtoStudy();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        removeWordButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                removeWordfromdataBase(selectWord);
                refreshWordList();
                card.setVisible(false);
                closeButton.setVisible(false);
                removeWordButton.setVisible(false);
            }
        });


    }

    private  void countWord(){
        int length = list.size();
        numberofWord.setText( numberOfWordwasStuddied()+"/" + length + " word");
    }
    @FXML
    private void handleMouseClickAWord(){
        selectWord = mywordList.getSelectionModel().getSelectedItem();
        idSelectWord  = mywordList.getSelectionModel().getSelectedIndex();
        selectDef  = listdef.get(idSelectWord);
        if (selectWord !=null){
            card.setText(selectWord);
            card.setVisible(true);
            closeButton.setVisible(true);
            removeWordButton.setVisible(true);
        }
    }
    private RotateTransition createRotator(Label card) {
        RotateTransition rotator = new RotateTransition(Duration.millis(800), card);
        rotator.setAxis(Rotate.X_AXIS);

        if (isFrontShowing) {
            rotator.setFromAngle(0);
            rotator.setToAngle(360);
            closeButton.setVisible(false);
            removeWordButton.setVisible(false);
        } else {
            rotator.setFromAngle(360);
            rotator.setToAngle(0);
            closeButton.setVisible(false);
            removeWordButton.setVisible(false);
        }
        rotator.setInterpolator(Interpolator.LINEAR);
        return rotator;
    }

    private PauseTransition changeCardFace(Label card) {
        PauseTransition pause = new PauseTransition(Duration.millis(800));

        if (isFrontShowing) {
            pause.setOnFinished(
                    e -> {
                        card.setText(selectDef);
                        closeButton.setVisible(true);
                        removeWordButton.setVisible(true);
                    });
        } else {
            pause.setOnFinished(
                    e -> {
                        card.setText(selectWord);
                        closeButton.setVisible(true);
                        removeWordButton.setVisible(true);
                    });
        }
        return pause;
    }

    private void  refreshWordList(){
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).equals(selectWord)) {
                list.remove(i);
                break;
            }
        mywordList.setItems(list);
    }

    private int numberOfWordwasStuddied(){
        try {
            String sql = "SELECT COUNT(*) FROM myWord WHERE status = 1";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {

                    return resultSet.getInt(1);
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }



}
