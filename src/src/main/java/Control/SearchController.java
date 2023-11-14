package Control;

import Commandline.DictionaryManagement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    @FXML
    private ImageView ExitIcon;

    @FXML
    private Label LabelKetQua;

    @FXML
    private Button smallSearch;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       ExitIcon.setOnMouseClicked(event ->{
            System.exit(0);
       });

    }
}
