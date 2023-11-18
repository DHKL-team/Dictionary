package Control;

import Commandline.DictionaryManagement;
import Commandline.Word;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import Commandline.DictionaryCommandLine;

public class SearchController implements Initializable {
    static boolean flag = true;


    @FXML
    private Label LabelKetQua;

    @FXML
    private AnchorPane paneSwitch;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> similarLabel;

    @FXML
    private JFXButton smallSearch;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

             searchField.setOnKeyTyped(new EventHandler<KeyEvent>() {

                 @Override
                 public void handle(KeyEvent keyEvent) {
                     try {
                         search();
                     } catch (FileNotFoundException e) {
                         throw new RuntimeException(e);
                     }
                 }
             });
             smallSearch.setOnMouseClicked(new EventHandler<MouseEvent>() {
                 @Override
                 public void handle(MouseEvent event) {
                     try {
                         search();
                     } catch (FileNotFoundException e) {
                         throw new RuntimeException(e);
                     }
                 }
             });
    }

    public void search() throws FileNotFoundException {
        String target = searchField.getText();
        if (target != null) {
            DictionaryCommandLine dictionaryCommandLine = new DictionaryCommandLine();
            if (flag) {
                dictionaryCommandLine.dictionaryBasic();
                flag = false;
            }
            String str = dictionaryCommandLine.dictionaryLookup(target);
            LabelKetQua.setText(str);
            System.out.println(str);
        }
    }

}
