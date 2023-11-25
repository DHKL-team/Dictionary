package Control;

import Alert.Alerts;
import Commandline.Dictionary;
import Commandline.DictionaryCommandLine;
import Commandline.DictionaryManagement;
import Commandline.Word;
import Database.DatabaseController;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddController extends DatabaseController implements Initializable {

    @FXML
    private TextField add_EN;

    @FXML
    private TextField add_VN;

    @FXML
    private JFXButton confirmButton;

    @FXML
    private ListView<String> suggestList;

    private Alerts alerts = new Alerts();
    DictionaryManagement dictionaryManagement = DictionaryManagement.getInstance();

    DictionaryCommandLine dictionaryCommandLine = DictionaryCommandLine.getInstance();
    ObservableList<String> list = FXCollections.observableArrayList();
    ObservableList<Word> listWord = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        suggestList.setVisible(false);

        confirmButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String tagert = add_EN.getText().trim();
                String explain = add_VN.getText().trim();
                String pronunciation = "";
                int tmp = explain.lastIndexOf('/');
                pronunciation = explain.substring(0, tmp + 1);
                pronunciation = pronunciation.trim();
                explain = explain.substring(tmp + 1);
                explain = explain.trim() + "\n\n";
                System.out.println(tmp + " " + pronunciation + "haha/n" + explain);
                Alert alertConfirm = alerts.alertConfirmation("ADD WORD","Do you want to add this word?") ;
                Optional<ButtonType> option = alertConfirm.showAndWait();
                if (option.get() == ButtonType.OK){
                    alerts.showAlertInfo("Word: " + tagert,"Add word to the dictionary successfully!");
                    dictionaryManagement.addData(tagert, pronunciation, explain);
                }
                else if (option.get() == ButtonType.CANCEL){
                    alerts.showAlertInfo("Information", "Changes are not recognized");
                }
            }

        });

        add_EN.setOnKeyTyped(new EventHandler<KeyEvent>() {
            
            public void handle(KeyEvent keyEvent) {
               String tagert = add_EN.getText();
               findWordExist(tagert);
            }


        });
    }
    
    public void findWordExist(String target){
        list.clear();

        listWord = dictionaryCommandLine.dictionarySearch(Dictionary.getInstance().getRoot(), target);
        for (Word w : listWord) {
            list.add(w.getWord_target());
        }
        suggestList.setItems(list);
        suggestList.setVisible(true);

    }


}
