package Game;
import  Commandline.Word;


import java.util.Scanner;

public class Hangman extends Game{
    private char[] guessedLetters;
    public Hangman(Word word) {
        super();
        this.turns = 6;
        this.word = word;
        guessedLetters = new char[word.getWord_target().length()];
        initializeGuessedLetters();
    }
    public void L_Hold() {
        System.out.println("  ____");
        System.out.println(" |    |");
        System.out.println(" |");
        System.out.println(" |");
        System.out.println(" |");

    }

    public void hangHead() {
        System.out.println("  ____");
        System.out.println(" |    |");
        System.out.println(" |    O");
        System.out.println(" |");
        System.out.println(" |");
    }

    public void hangBody() {
        System.out.println("  ____");
        System.out.println(" |    |");
        System.out.println(" |    O");
        System.out.println(" |    |");
        System.out.println(" |");
    }

    public void hangArm1() {
        System.out.println("  ____");
        System.out.println(" |    |");
        System.out.println(" |    O");
        System.out.println(" |   /|");
        System.out.println(" |");
    }
    public void hangArm2() {
        System.out.println("  ____");
        System.out.println(" |    |");
        System.out.println(" |    O");
        System.out.println(" |   /|\\");
        System.out.println(" |");
    }
    public void hangLeg1() {
        System.out.println("  ____");
        System.out.println(" |    |");
        System.out.println(" |    O");
        System.out.println(" |   /|\\");
        System.out.println(" |   /");
    }
    public void hangLeg2() {
        System.out.println("  ____");
        System.out.println(" |    |");
        System.out.println(" |    O");
        System.out.println(" |   /|\\");
        System.out.println(" |   / \\");
        System.out.println("Game over! The word was: " + this.word.getWord_target());
    }
    public boolean isHangMan() {
        return this.turns == 0;
    }

    private void initializeGuessedLetters() {
        for (int i = 0; i < this.word.getWord_target().length(); i++) {
            guessedLetters[i] = '_';
        }
    }

    private void displayProgress() {
        System.out.println("Current progress: " + new String(guessedLetters));
    }

    private void displayHangman() {
        int remainingAttempts = this.turns + 1; // Số lần còn lại trước khi thua
        System.out.println("Attempts left: " + remainingAttempts);
        switch (this.turns) {
            case 6:
                L_Hold();
                break;
            case 5:
                hangHead();
                break;
            case 4:
                hangBody();
                break;
            case 3:
                hangArm1();
                break;
            case 2:
                hangArm2();
                break;
            case 1:
                hangLeg1();
                break;
            case 0:
                hangLeg2();
                break;
        }
    }

    private boolean isGameOver() {
        return this.turns == 0 || new String(guessedLetters).equals(this.word.getWord_target());
    }

    private boolean makeGuess(char letter) {
        boolean correctGuess = false;
        for (int i = 0; i < this.word.getWord_target().length(); i++) {
            if (this.word.getWord_target().charAt(i) == letter) {
                guessedLetters[i] = letter;
                correctGuess = true;
            }
        }
        if (!correctGuess) {
            this.turns--;
        }
        return correctGuess;
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(this.word.getWord_explain());
        while (!isGameOver()) {
            displayProgress();
            displayHangman();

            System.out.print("Enter a letter: ");
            char guess = scanner.next().charAt(0);

            if (!Character.isLetter(guess)) {
                System.out.println("Please enter a valid letter.");
                continue;
            }

            if (makeGuess(guess)) {
                System.out.println("Good guess!");
            } else {
                System.out.println("Incorrect guess.");
            }
        }

    }
}