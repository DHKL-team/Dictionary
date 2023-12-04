package Control;

import java.io.IOException;
import java.lang.module.Configuration;
import java.net.URL;
import java.util.ResourceBundle;

import com.darkprograms.speech.translator.GoogleTranslate;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class GoogleTranslator implements Initializable {
    private String input;
    private String output;
    private String source = "en";
    private String target = "vi";
    @FXML
    private ImageView loudspeak;
    @FXML
    private TextArea intxt;
    @FXML
    private TextArea outtxt;
    @FXML
    private ChoiceBox<String> sourceLan;
    @FXML
    private ChoiceBox<String> targetLan;
    @FXML
    private ImageView ExchangeButton;

    public void setsource(String s) {
        if (s.equals("Vietnamese")) source = "vi";
        else if (s.equals("English")) source = "en";
        else if (s.equals("Spanish")) source = "es";
        else if (s.equals("Hindi")) source = "hi";
        else if (s.equals("Arabic")) source = "ar";
        else if (s.equals("Portuguese")) source = "pt";
        else if (s.equals("Russian")) source = "ru";
        else if (s.equals("French")) source = "fr";
        else if (s.equals("German")) source = "de";
        else source = "zh-CN";
    }

    public void settarget(String t) {
        if (t.equals("Vietnamese")) target = "vi";
        else if (t.equals("English")) target = "en";
        else if (t.equals("Spanish")) target = "es";
        else if (t.equals("Hindi")) target = "hi";
        else if (t.equals("Arabic")) target = "ar";
        else if (t.equals("Portuguese")) target = "pt";
        else if (t.equals("Russian")) target = "ru";
        else if (t.equals("French")) target = "fr";
        else if (t.equals("German")) target = "de";
        else target = "zh-CN";
    }

    @FXML
    public void Translate(ActionEvent event) throws IOException {

        try {
            input = intxt.getText();
            if (input != null) {
                output = GoogleTranslate.translate(source, target, input);
                outtxt.setText(output);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Can't connect to Internet!");
            alert.show();
        }
    }

    // Chuyen van ban thanh giong noi
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

            // voice.setRate(130); //voice.setVolume((float) 0.9); voice.setPitch(120);

            voice.speak(input);

            voice.deallocate();
        } else {
            System.out.println("Error in getting Voices");
        }
    }

    // chuyen giong noi thanh van ban
    @FXML
    public void SpeechToText(ActionEvent event) {
		/*
		Configuration configuration = new Configuration();

		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

		LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
		// Start recognition process pruning previously cached data.
		recognizer.startRecognition(true);
		SpeechResult result = recognizer.getResult();
		// Pause recognition process. It can be resumed then with startRecognition(false).
		recognizer.stopRecognition();

		recognizer.startRecognition(stream);
		SpeechResult result;
		while ((result = recognizer.getResult()) != null) {
			System.out.format("Hypothesis: %s\n", result.getHypothesis());
		}
		recognizer.stopRecognition();
		*/

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<String> choice1 = FXCollections.observableArrayList("English", "Vietnamese", "Chinese", "Spanish", "Hindi", "Arabic", "Portuguese", "Russian", "French", "German");
        sourceLan.setItems(choice1);
        sourceLan.setValue("English");
        sourceLan.setOnAction(e -> setsource(sourceLan.getValue()));

        ObservableList<String> choice2 = FXCollections.observableArrayList("English", "Vietnamese", "Chinese", "Spanish", "Hindi", "Arabic", "Portuguese", "Russian", "French", "German");
        targetLan.setItems(choice2);
        targetLan.setValue("Vietnamese");
        targetLan.setOnAction(e -> settarget(targetLan.getValue()));


        ExchangeButton.setOnMouseClicked(e -> exchangeLanguage());


    }

    private void exchangeLanguage() {
        String temp = sourceLan.getValue();
        sourceLan.setValue(targetLan.getValue());
        targetLan.setValue(temp);
        setsource(sourceLan.getValue());
        settarget(targetLan.getValue());

        String tmp1 = outtxt.getText();
        outtxt.setText(intxt.getText());
        intxt.setText(tmp1);
    }

}