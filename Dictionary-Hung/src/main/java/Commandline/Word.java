package Commandline;

public class Word {
    private String word_target;
    private String word_pronunciation;
    private String word_explain;

    private boolean word_favourite;

    public Word(String target, String explain) {
        target = target.toLowerCase().trim();
        this.word_target = target;
        this.word_explain = explain;
        this.word_pronunciation = "";
        this.word_favourite = false;
    }

    public Word(String target, String pronunciation, String explain) {
        target = target.toLowerCase().trim();
        this.word_target = target;
        this.word_pronunciation = pronunciation;
        this.word_explain = explain;
        this.word_favourite = false;
    }

    public void addWord_explain(String explain) {
        word_explain += explain;
    }

    //getter
    public String getWord_target() {
        return word_target;
    }

    // setter
    public void setWord_target(String target) {
        word_target = target;
    }

    public String getWord_explain() {
        return word_explain;
    }

    public void setWord_explain(String explain) {
        word_explain = explain;
    }

    public String getWord_pronunciation() {
        return word_pronunciation;
    }

    public void setWord_pronunciation(String pronunciation) {
        word_pronunciation = pronunciation;
    }

    public boolean getWord_favourite() {
        return word_favourite;
    }

    public void setWord_favourite(boolean favourite) {
        word_favourite = favourite;
    }

}

