package Control;

import Commandline.Dictionary;
import Commandline.DictionaryManagement;
import Commandline.Word;
import com.jfoenix.controls.JFXButton;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    DictionaryCommandLine dictionaryCommandLine = DictionaryCommandLine.getInstance();
    @FXML
    private ImageView deleteButton;

    @FXML
    private ImageView updateButton;

    @FXML
    private TextArea LabelKetQua;

    @FXML
    private AnchorPane paneSwitch;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> similarLabel;

    @FXML
    private ImageView smallSearch;

    private int indexOfSelectedWord;
    ObservableList<Word> listWord = FXCollections.observableArrayList();
    ObservableList<String> list = FXCollections.observableArrayList();

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

        updateButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                 LabelKetQua.setEditable(true);
                 //saveButton.setVisible(true);

            }
        });
        LabelKetQua.setEditable(false);
    }

    public void search() throws FileNotFoundException {
        list.clear();
        String target = searchField.getText().trim();
        listWord = dictionaryCommandLine.dictionarySearch(Dictionary.getInstance().getRoot(), target);
        for (Word w : listWord) {
            list.add(w.getWord_target());
        }
        similarLabel.setItems(list);
        String str = dictionaryCommandLine.dictionaryLookup(target);
        LabelKetQua.setText(str);
        System.out.println(str);
    }


    public void setListDefault(int index) {
        if (index == 0) return;
    }

    @FXML
    private void handleMouseClickAWord(MouseEvent e){
        String selectWord = similarLabel.getSelectionModel().getSelectedItem();
        if (selectWord !=null){
            String str = dictionaryCommandLine.dictionaryLookup(selectWord);
            LabelKetQua.setText(str);
        }
    }

    @FXML
    private void clickSaveButton(){

    }

}