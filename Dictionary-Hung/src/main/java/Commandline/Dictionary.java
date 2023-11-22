package Commandline;

public class

Dictionary extends Trie {
    private Dictionary() {

    }

    public static Dictionary getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final Dictionary INSTANCE = new Dictionary();
    }
}