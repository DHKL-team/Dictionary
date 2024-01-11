package Control;

import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.swing.SwingWorker;

import com.jfoenix.controls.JFXButton;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import Alert.Alerts;
import Commandline.Dictionary;
import Commandline.DictionaryCommandLine;
import Commandline.DictionaryManagement;
import Commandline.Word;
import Database.DatabaseController;
import STT.microphone.Microphone;
import STT.recognizer.GSpeechDuplex;
import javaFlacEncoder.FLACFileWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import STT.recognizer.GSpeechResponseListener;
import STT.recognizer.GoogleResponse;

public class SearchController extends DatabaseController implements Initializable {

	DictionaryCommandLine dictionaryCommandLine = DictionaryCommandLine.getInstance();

	DictionaryManagement dictionaryManagement = DictionaryManagement.getInstance();

	@FXML
	private JFXButton saveButton;

	@FXML
	private JFXButton favorButton;

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

	@FXML
	private Label tagertResult;

	@FXML
	private Label pronunLabel;

	@FXML
	private Button record;

	private String target = "";
	private String explain = "";

	private String pronunciation = "";
	private Alerts alerts = new Alerts();
	private int indexOfSelectedWord;
	ObservableList<Word> listWord = FXCollections.observableArrayList();
	ObservableList<String> list = FXCollections.observableArrayList();
	
	final Microphone mic = new Microphone(FLACFileWriter.FLAC);
    final GSpeechDuplex duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		duplex.setLanguage("en");
		duplex.addResponseListener(new GSpeechResponseListener() {
            String old_text = "";
            public void onResponse(GoogleResponse gr) {
            	//mic.open();
                String output = "";
                output = gr.getResponse();
                if (gr.getResponse() == null) {
                    this.old_text = searchField.getText();
                    if (this.old_text.contains("("))
                        this.old_text = this.old_text.substring(0, this.old_text.indexOf('('));
                    System.out.println("Paragraph Line Added");
                    this.old_text = String.valueOf(searchField.getText()) + "\n";
                    this.old_text = this.old_text.replace(")", "").replace("( ", "");
                    searchField.setText(this.old_text);
                    return;
                }
                if (output.contains("("))
                    output = output.substring(0, output.indexOf('('));
                if (!gr.getOtherPossibleResponses().isEmpty())
                    output = String.valueOf(output) + " (" + (String)gr.getOtherPossibleResponses().get(0) + ")";
                System.out.println(output);
                searchField.setText("");
                searchField.appendText(this.old_text);
                searchField.appendText(output);
            }
        });
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
		connectdataBase();
	}

	public void search() throws FileNotFoundException {
		list.clear();
		target = searchField.getText().trim();
		target = target.toLowerCase();
		listWord = dictionaryCommandLine.dictionarySearch(Dictionary.getInstance().getRoot(), target);
		for (Word w : listWord) {
			list.add(w.getWord_target());
		}
		similarLabel.setItems(list);
		explain = dictionaryManagement.dictionaryLookup(target);
		int tmp = explain.lastIndexOf('/');
		pronunciation = explain.substring(0, tmp + 1);
		pronunciation = pronunciation.trim();
		explain = explain.substring(tmp + 1);
		explain = explain.trim() + "\n\n";
		tagertResult.setText(target.toUpperCase() + "\n");
		pronunLabel.setText(pronunciation);
		LabelKetQua.setText(explain);
		System.out.println(explain);
	}

	public void setListDefault(int index) {
		if (index == 0)
			return;
	}

	@FXML
	private void handleMouseClickAWord(MouseEvent e) {
		String selectWord = similarLabel.getSelectionModel().getSelectedItem();

		if (selectWord != null) {
			target = selectWord;
			explain = dictionaryCommandLine.dictionaryLookup(selectWord);
			int tmp = explain.lastIndexOf('/');
			pronunciation = explain.substring(0, tmp + 1);
			pronunciation = pronunciation.trim();
			explain = explain.substring(tmp + 1);
			explain = explain.trim() + "\n\n";
			tagertResult.setText(target.toUpperCase() + "\n");
			pronunLabel.setText(pronunciation);
			LabelKetQua.setText(explain);
		}
	}

	@FXML
	private void clickupdateButton() {
		LabelKetQua.setEditable(true);
		saveButton.setVisible(true);
		alerts.showAlertInfo("Information", "You can edit words !");
	}

	@FXML
	private void clickSaveButton() {
		explain = LabelKetQua.getText().trim();
		int tmp = explain.lastIndexOf('/');
		pronunciation = explain.substring(0, tmp + 1);
		pronunciation = pronunciation.trim();
		explain = explain.substring(tmp + 1);
		explain = explain.trim() + "\n\n";
		Alert alertConfirm = alerts.alertConfirmation("Update", "Would you like to update the meaning of: " + target);
		Optional<ButtonType> option = alertConfirm.showAndWait();
		if (option.get() == ButtonType.OK) {
			dictionaryManagement.updateData(target, explain);
			alerts.showAlertInfo("Information", "Updated word successfully!");
		} else
			alerts.showAlertInfo("Information", "Update failed!");
		saveButton.setVisible(false);
		LabelKetQua.setEditable(false);
	}

	@FXML
	private void clickdeleteButton() {
		Alert alertConfirm = alerts.alertConfirmation("Delete:", "Would you like to delete this word !");
		Optional<ButtonType> option = alertConfirm.showAndWait();
		if (option.get() == ButtonType.OK) {
			dictionaryManagement.removeData(target);
			alerts.showAlertInfo("Information", "Deleted successfully!");
			refreshlistWord();
		} else
			alerts.showAlertInfo("Information", "Delete failed!");
	}

	private void refreshlistWord() {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).equals(target)) {
				list.remove(i);
				break;
			}
		similarLabel.setItems(list);
	}

	@FXML
	private void clickfavorButton() {

		if (target != null && !target.isEmpty())
			addwordtodataBase(target, explain);
	}

	@FXML
	public void TextToSpeech(ActionEvent event) {
		final String VOICE_KEY = "freetts.voices";
		final String VOICE_VALUE = "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory";
		System.setProperty(VOICE_KEY, VOICE_VALUE);
		Voice voice = VoiceManager.getInstance().getVoice("kevin16");
		System.setProperty(VOICE_KEY, VOICE_VALUE);
		Voice[] voicelist = VoiceManager.getInstance().getVoices();
		if (voice != null) {
			voice.allocate();
			/*
			 * voice.setRate(130); //voice.setVolume((float) 0.9); voice.setPitch(120);
			 */
			/*
			 * System.out.println("Voice Rate: "+ voice.getRate());
			 * System.out.println("Voice Pitch: "+ voice.getPitch());
			 * System.out.println("Voice Volume: "+ voice.getVolume());
			 */
			voice.speak(target);

			voice.deallocate();
		} else {
			System.out.println("Error in getting Voices");
		}
	}
	
	@FXML
    public void MicFunc(ActionEvent event) {
		mic.open();
        Button stop = new Button("Stop");
        stop.setLayoutX(475); // khong dung duoc record.getLayoutX(), dung so nay de nut STOP trung nut MIC
        stop.setLayoutY(record.getLayoutY());
        stop.setOnAction(e -> StopFunc(stop));
        paneSwitch.getChildren().add(stop);
        
        // Start the recognition process in a separate thread
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
        	@Override
        	protected Void doInBackground() {
        		try {
        			duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
        		} catch (Exception ex) {
        			ex.printStackTrace();
        		}
        		return null;
        	}

        	@Override
        	protected void done() {
              
        	}
        };
        
        worker.execute();   
	} 
	
	public void StopFunc(Button b) {
		mic.close();
		paneSwitch.getChildren().remove(b);
	}

}