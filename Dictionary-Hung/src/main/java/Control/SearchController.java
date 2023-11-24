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
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import Alert.Alerts;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import Commandline.DictionaryCommandLine;
import javafx.scene.web.WebView;

public class SearchController implements Initializable {

    DictionaryCommandLine dictionaryCommandLine = DictionaryCommandLine.getInstance();

    DictionaryManagement dictionaryManagement = DictionaryManagement.getInstance();
    @FXML
    private JFXButton deleteButton;

   /* @FXML
    private WebView view = new WebView();*/

    @FXML
    private  JFXButton saveButton;

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

    private   String target="";
    private  Alerts alerts = new Alerts();
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
        LabelKetQua.setEditable(false);
        saveButton.setVisible(false);

    }

    public void search() throws FileNotFoundException {

        list.clear();
        target = searchField.getText().trim();
        listWord = dictionaryCommandLine.dictionarySearch(Dictionary.getInstance().getRoot(), target);
        for (Word w : listWord) {
            list.add(w.getWord_target());
        }
        similarLabel.setItems(list);
        String str = dictionaryManagement.dictionaryLookup(target);
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
            target = selectWord;
            String str = dictionaryCommandLine.dictionaryLookup(selectWord);
            LabelKetQua.setText(str);
        }
    }

    @FXML
    private  void clickupdateButton(){
        LabelKetQua.setEditable(true);
        saveButton.setVisible(true);
        alerts.showAlertInfo("Information","You can edit words !");
    }
    @FXML
    private void clickSaveButton(){
        String explain = LabelKetQua.getText().trim();
        String pronunciation = "";
        int tmp = explain.lastIndexOf('/');
        pronunciation = explain.substring(0, tmp + 1);
        pronunciation = pronunciation.trim();
        explain = explain.substring(tmp + 1);
        explain = explain.trim() + "\n\n";
        Alert alertConfirm = alerts.alertConfirmation("Update","Would you like to update the meaning of: "+ target);
        Optional<ButtonType> option = alertConfirm.showAndWait();
        if (option.get() == ButtonType.OK){
            dictionaryManagement.updateData(target,explain);
            alerts.showAlertInfo("Information","Updated word successfully!");
        }
        else alerts.showAlertInfo("Information","Update failed!");
        saveButton.setVisible(false);
        LabelKetQua.setEditable(false);
    }

    @FXML
    private void clickdeleteButton(){
        Alert alertConfirm = alerts.alertConfirmation("Delete:","Would you like to delete this word !");
        Optional<ButtonType> option = alertConfirm.showAndWait();
        if (option.get() == ButtonType.OK){
            dictionaryManagement.removeData(target);
            alerts.showAlertInfo("Information","Deleted successfully!");
            refreshlistWord();
        }
        else alerts.showAlertInfo("Information", "Delete failed!");
    }

    private void refreshlistWord(){
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).equals(target)) {
                list.remove(i);
                break;
            }
       similarLabel.setItems(list);
    }
}