package Commandline;

public class Word {
    private String word_target;
    private String word_explain;

    public Word(String target, String explain) {
        this.word_target = target;
        this.word_explain = explain;
    }
    // setter
    public void setWord_target(String target) {
        word_target = target;
    }
    public void setWord_explain(String explain) {
        word_explain = explain;
    }
    //getter
    public String getWord_target() {
        return word_target;
    }
    public  String getWord_explain() {
        return word_explain;
    }


}

