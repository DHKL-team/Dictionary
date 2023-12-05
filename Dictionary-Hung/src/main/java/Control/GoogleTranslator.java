package Control;

import java.io.IOException;
import java.lang.module.Configuration;
import java.net.URL;
import java.util.ResourceBundle;

import STT.microphone.Microphone;
import STT.recognizer.GSpeechDuplex;
import STT.recognizer.GSpeechResponseListener;
import STT.recognizer.GoogleResponse;
import com.darkprograms.speech.translator.GoogleTranslate;
import com.jfoenix.controls.JFXButton;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import javaFlacEncoder.FLACFileWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;

public class GoogleTranslator implements Initializable {
    private String input;
    private String output;
    private String source = "en";
    private String target = "vi";

    final Microphone mic = new Microphone(FLACFileWriter.FLAC);
    final GSpeechDuplex duplex = new GSpeechDuplex("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
    public void setsource(String s) {
        if(s.equals("Vietnamese")) source = "vi";
        else if(s.equals("English")) source = "en";
        else if(s.equals("Spanish")) source = "es";
        else if(s.equals("Hindi")) source = "hi";
        else if(s.equals("Korean")) source = "ko";
        else if(s.equals("Portuguese")) source = "pt";
        else if(s.equals("Russian")) source = "ru";
        else if(s.equals("French")) source = "fr";
        else if(s.equals("German")) source = "de";
        else if(s.equals("Japanese")) source = "ja";
        else source = "zh-CN";
    }

    public void settarget(String t) {
        if(t.equals("Vietnamese")) target = "vi";
        else if(t.equals("English")) target = "en";
        else if(t.equals("Spanish")) target = "es";
        else if(t.equals("Hindi")) target = "hi";
        else if(t.equals("Korean")) target = "ko";
        else if(t.equals("Portuguese")) target = "pt";
        else if(t.equals("Russian")) target = "ru";
        else if(t.equals("French")) target = "fr";
        else if(t.equals("German")) target = "de";
        else if(t.equals("Japanese")) source = "ja";
        else target = "zh-CN";
    }
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
    private AnchorPane paneSwitch;

    @FXML
    private ImageView ExchangeButton;

    @FXML
    private JFXButton record;
    @FXML
    public void Translate(ActionEvent event) throws IOException {

        try {
            input = intxt.getText();
            if (input != null) {
                output = GoogleTranslate.translate(source, target , input);
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
        input = intxt.getText();
        final String VOICE_KEY = "freetts.voices";
        final String VOICE_VALUE = "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory";
        System.setProperty(VOICE_KEY, VOICE_VALUE);
        Voice voice = VoiceManager.getInstance().getVoice("kevin16");
        System.setProperty(VOICE_KEY, VOICE_VALUE);
        Voice[] voicelist = VoiceManager.getInstance().getVoices();
        if (voice != null) {
            voice.allocate();

             // voice.setRate(130); //voice.setVolume((float) 0.9); voice.setPitch(120);

            if(input != null && !input.equals("[\s]")) voice.speak(input);

            voice.deallocate();
        } else {
            System.out.println("Error in getting Voices");
        }
    }

    // chuyen giong noi thanh van ban
    @FXML
    public void SpeechToText(ActionEvent event) {
        mic.open();
        duplex.setLanguage(source);
        Button stop = new Button("Stop");
        stop.getStyleClass().add("stop");
        stop.setLayoutX(record.getLayoutX()); // khong dung duoc record.getLayoutX()
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<String> choice1 = FXCollections.observableArrayList("English", "Vietnamese", "Chinese", "Spanish", "Hindi", "Korean", "Portuguese", "Russian", "French", "German","Japanese");
        sourceLan.setItems(choice1);
        sourceLan.setValue("English");
        sourceLan.setOnAction(e -> setsource(sourceLan.getValue()));

        ObservableList<String> choice2 = FXCollections.observableArrayList("English", "Vietnamese", "Chinese", "Spanish", "Hindi", "Korean", "Portuguese", "Russian", "French", "German","Japanese");
        targetLan.setItems(choice2);
        targetLan.setValue("Vietnamese");
        targetLan.setOnAction(e -> settarget(targetLan.getValue()));


        ExchangeButton.setOnMouseClicked(e -> exchangeLanguage());

        duplex.setLanguage(source);
        duplex.addResponseListener(new GSpeechResponseListener() {
            String old_text = "";
            public void onResponse(GoogleResponse gr) {
                //mic.open();
                String output = "";
                output = gr.getResponse();
                if (gr.getResponse() == null) {
                    this.old_text = intxt.getText();
                    if (this.old_text.contains("("))
                        this.old_text = this.old_text.substring(0, this.old_text.indexOf('('));
                    System.out.println("Paragraph Line Added");
                    this.old_text = String.valueOf(intxt.getText()) + "\n";
                    this.old_text = this.old_text.replace(")", "").replace("( ", "");
                    intxt.setText(this.old_text);
                    return;
                }
                if (output.contains("("))
                    output = output.substring(0, output.indexOf('('));
                if (!gr.getOtherPossibleResponses().isEmpty())
                    output = String.valueOf(output) + " (" + (String)gr.getOtherPossibleResponses().get(0) + ")";
                System.out.println(output);
                intxt.setText("");
                intxt.appendText(this.old_text);
                intxt.appendText(output);
            }
        });

    }

    private void exchangeLanguage(){
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