package Commandline;

public class Word {
    private String word_target;
    private String word_pronunciation;
    private String word_explain;


    public Word(String target, String explain) {
        this.word_target = target;
        this.word_explain = explain;
        this.word_pronunciation = null;
    }

    public Word(String target, String pronunciation, String explain) {
        this.word_target = target;
        this.word_pronunciation = pronunciation;
        this.word_explain = explain;
    }
    // setter
    public void setWord_target(String target) {
        word_target = target;
    }
    public void setWord_explain(String explain) {
        word_explain = explain;
    }
    public void setWord_pronunciation(String pronunciation) {
        word_pronunciation = pronunciation;
    }
    public void addWord_explain(String explain) {
        word_explain += explain;
    }

    //getter
    public String getWord_target() {
        return word_target;
    }
    public  String getWord_explain() {
        return word_explain;
    }
    public String getWord_pronunciation() {
        return word_pronunciation;
    }

}

