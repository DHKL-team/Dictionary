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
    protected int hp;

    protected Random rand;

    public Game() throws FileNotFoundException {
        this.score = 0;
        rand = new Random();
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
        while (target.trim().contains(" ") || target.contains("-") || target.trim().length() > 12) {
            value = rand.nextInt(55000);
            target = wordList.get(value).getWord_target();
        }
        return wordList.get(value);
    }

    public void switchToGameMenu() throws IOException {
        URL url = new File("./src/main/resources/Commandline/MenuGame.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root, 1030, 679);
        Main.stageRefer.setScene(scene);
        Main.moveScreen(root, Main.stageRefer);
        Main.stageRefer.show();
    }

    public void ExitFunc() {
        System.exit(0);
    }

}