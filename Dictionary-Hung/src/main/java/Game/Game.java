package Game;

import Commandline.DictionaryCommandLine;
import Commandline.Main;
import Commandline.Word;
import Database.DatabaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class Game extends DatabaseController {
    protected int turns;
    protected int score;
    protected List<Word> wordList;

    Random rand;

    protected int hp;
    public Game() throws FileNotFoundException {
        rand = new Random();
        this.score = 0;
        wordList = DictionaryCommandLine.getInstance().getWordList();
    }
    public int getTurns() {
        return turns;
    }
    public void setTurns(int turns) {
        this.turns = turns;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public Word randomWord() {
        int value = rand.nextInt(55000);
        String target = wordList.get(value).getWord_target();
        while (target.contains(" ") || target.contains("-")) {
            value = rand.nextInt(55000);
        }
        return wordList.get(value);
    }

    public void switchtoGameMenu() throws IOException {
        URL url = new File("./src/main/resources/Commandline/MenuGame.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root,1030,679);
        Main.stageRefer.setScene(scene);
        Main.moveScreen(root,Main.stageRefer);
        Main.stageRefer.show();
    }

    public void ExitFunc(){
        System.exit(0);
    }

}