import java.util.Arrays;

public class Trie {
    static final int ALPHABET_SIZE = 26;

    public static class TrieNode {
        TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        boolean isEndOfWord;
        Word word;

        TrieNode() {
            isEndOfWord = false;
            word = null;
            Arrays.fill(children, null);
        }
    };

    public static TrieNode root = new TrieNode();

    public TrieNode getRoot() {
        return root;
    }
    public void insert(String key, String value) {
        int level;
        int length = key.length();
        int index;
        TrieNode p = root;
        for (level = 0; level < length; level++) {
            index = key.charAt(level) - 'a';
            if (p.children[index] == null) {
                p.children[index] = new TrieNode();
            }
            p = p.children[index];
        }
        p.isEndOfWord = true;
        p.word = new Word(key, value);
    }
    public Word search(String key) {
        int level;
        int length = key.length();
        int index;
        TrieNode p = root;
        for (level = 0; level < length; level++) {
            index = key.charAt(level) - 'a';
            if (index < 0 || p.children[index] == null) {
                return null;
            }
            p = p.children[index];
        }
        if (p.isEndOfWord) {
            return p.word;
        }
        return null;
    }
    public Word remove(String key) {
        int level;
        int length = key.length();
        int index;
        TrieNode p = root;
        for (level = 0; level < length; level++) {
            index  = key.charAt(level) - 'a';
            if (p.children[index] == null) {
                return null;
            }
            p = p.children[index];
        }
        if (p.isEndOfWord) {
            Word tmp = p.word;
            p.word = null;
            p.isEndOfWord = false;
            return tmp;
        }
        return null;
    }
}
