package Game;
import Commandline.Word;
import  Commandline.Dictionary;
import java.util.Random;
import java.util.Scanner;

public class Game{
    protected Word word;
    protected int turns;
    protected int score;
    public Game() {
        this.score = 0;
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
    /**
     * get random word index from 0 to 1000.
     *
     * @return Word.
     */
    public Word getRandomWord(Dictionary dictionary) {
        Random rand = new Random();
        int randomIndex = rand.nextInt(150);
        return null;
    }
}