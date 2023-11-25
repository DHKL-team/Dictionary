package Control;

import Database.DatabaseController;
import com.jfoenix.controls.JFXButton;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class MyWordControl extends  DatabaseController implements Initializable {
    @FXML
    private ListView<String> mywordList;

    @FXML
    private JFXButton studyButton;


    private ObservableList<String> list;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectdataBase();
        list = mywordList();
        mywordList.setItems(list);
    }


}
