package Control;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddController implements Initializable {

    @FXML
    private TextField add_EN;

    @FXML
    private TextField add_VN;

    @FXML
    private JFXButton confirmButton;

    @FXML
    private ListView<String> suggestList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        suggestList.setVisible(false);
    }
}
