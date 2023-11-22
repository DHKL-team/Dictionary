package Commandline;

import java.util.Arrays;

public class Trie {
    static final int ALPHABET_SIZE = 28;

    public static class TrieNode {
        TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        boolean isEndOfWord;
        Word word;

        TrieNode() {
            isEndOfWord = false;
            word = null;
            Arrays.fill(children, null);
        }
    }

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
            key = key.toLowerCase();
            if (key.charAt(level) == ' ') {
                index = 26;
            } else if (key.charAt(level) == '-') {
                index = 27;
            }
            else index = key.charAt(level) - 'a';
            if (index < 0 || index > 27) return;
            if (p.children[index] == null) {
                p.children[index] = new TrieNode();
            }
            p = p.children[index];
        }
        p.isEndOfWord = true;
        p.word = new Word(key, value);
    }

    public void insert(Word word) {
        int level;
        int length = word.getWord_target().length();
        int index;
        TrieNode p = root;
        for (level = 0; level < length; level++) {
            String key = word.getWord_target();
            key = key.toLowerCase();
            if (key.charAt(level) == ' ') {
                index = 26;
            } else if (key.charAt(level) == '-') {
                index = 27;
            }
            else index = key.charAt(level) - 'a';
            if (index < 0 || index > 27) return;
            if (p.children[index] == null) {
                p.children[index] = new TrieNode();
            }
            p = p.children[index];
        }
        p.isEndOfWord = true;
        p.word = word;
    }

    public Word search(String key) {
        int level;
        int length = key.length();
        int index;
        TrieNode p = root;
        for (level = 0; level < length; level++) {
            if (key.charAt(level) == ' ') {
                index = 26;
            } else if (key.charAt(level) == '-') {
                index = 27;
            }
            else index = key.charAt(level) - 'a';
            if (index < 0 || index > 27 || p.children[index] == null) return null;
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
            if (key.charAt(level) == ' ') {
                index = 26;
            } else if (key.charAt(level) == '-') {
                index = 27;
            }
            else index = key.charAt(level) - 'a';
            if (index < 0 || index > 27) return null;
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
