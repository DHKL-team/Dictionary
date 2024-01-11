package Commandline;


import Database.DatabaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
public class DictionaryCommandLine {

    private DictionaryCommandLine() {

    }

    public static DictionaryCommandLine getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final DictionaryCommandLine INSTANCE = new DictionaryCommandLine();
    }

    DictionaryManagement dictionaryMng = DictionaryManagement.getInstance();
    Dictionary dictionary = Dictionary.getInstance();
    List<Word> wordList = new ArrayList<>();
    boolean wordListChange = false;
    public List<Word> getWordList() {
        return wordList;
    }
    private void dfs(List<Word> list, Trie.TrieNode root) {
       try {
           for (int i = 0; i < Trie.ALPHABET_SIZE; i++) {
               Trie.TrieNode tmp = root.children[i];
               if (tmp != null) {
                   if (tmp.isEndOfWord) {
                       list.add(tmp.word);
                   }
                   dfs(list, tmp);
               }
           }
       } catch (NullPointerException e){
           System.out.println("Loi null pointerException");
       }

    }

    public void showAllWords() {
        System.out.println("No   | English         | Vietnamese");
        if (wordListChange) {
            wordList.clear();
            dfs(wordList, dictionary.getRoot());
        }
        wordListChange = false;
        for (int number = 0; number < wordList.size(); number++) {
            System.out.printf("%-5d| %-16s| %1s%n", number + 1, wordList.get(number).getWord_target(),
                    wordList.get(number).getWord_explain());
        }
    }
    public void dictionaryBasic() throws FileNotFoundException {
        String file = "./src/main/resources/Utils/tudien.txt";
        dictionaryMng.insertFromFile(file);
        wordListChange = true;
        wordList.clear();
        dfs(wordList, dictionary.getRoot());
    }

    public String dictionaryLookup(String target) {
        return dictionaryMng.dictionaryLookup(target);
    }


    public ObservableList<Word> dictionarySearch(Trie.TrieNode root, String target) {
        ObservableList<Word> list = FXCollections.observableArrayList();
        int index;
        for (int i = 0; i < target.length(); i++) {
            if (target.charAt(i) == ' ') {
                index = 0;
            } else if (target.charAt(i) == '-') {
                index = 1;
            }
            else index = target.charAt(i) - 'a' + 2;
            if (root != null){
            root = root.children[index];
            }
        }
        if (root!=null && root.isEndOfWord) {
            list.add(root.word);
        }
        dfs(list, root);
        return list;
    }

    public void dictionaryImportFromFile(String file) throws FileNotFoundException {
        dictionaryMng.insertFromFile(file);
    }

    public void dictionaryExportToFile() {
        try {
            FileWriter myWriter = new FileWriter("./src/main/resources/Utils/tudien.txt");
            if (wordListChange) {
                wordList.clear();
                dfs(wordList, dictionary.getRoot());
            }
            for (Word word : wordList) {
                myWriter.write(String.format("@%s %s\n%s", word.getWord_target()
                        , word.getWord_pronunciation(),
                        word.getWord_explain()));
            }
            myWriter.close();
            System.out.println("Exported to File");
        } catch (IOException e) {
            System.out.println("Failed to Export to File");
        }
    }

    public void dictionaryAdvanced() throws IOException {
        System.out.println("Welcome to My Application!");
        System.out.println("""
                [0] Exit
                [1] Add
                [2] Remove
                [3] Update
                [4] Display
                [5] Lookup
                [6] Search
                [7] Game
                [8] Import from file
                [9] Export to file""");
        Scanner scan = new Scanner(System.in);
        String target, explain;
        while (scan.hasNext()) {
            int i = scan.nextInt();
            switch (i) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    target = scan.next();
                    explain = scan.nextLine();
                    explain = explain.trim();
                    dictionaryMng.addData(target, explain);
                    wordListChange = true;
                    break;
                case 2:
                    target = scan.next();
                    dictionaryMng.removeData(target);
                    wordListChange = true;
                    break;
                case 3:
                    target = scan.next();
                    explain = scan.nextLine();
                    explain = explain.trim();
                    dictionaryMng.updateData(target, explain);
                    wordListChange = true;
                    break;
                case 4:
                    showAllWords();
                    break;
                case 5:
                    target = scan.next();
                    System.out.println(dictionaryLookup(target));
                    break;
                case 6:
                    target = scan.next();
                    List<Word> list = dictionarySearch(dictionary.getRoot(), target);
                    for (Word word : list) {
                        System.out.println(word.getWord_target());
                    }
                    break;
                case 7:
                case 8:
                    String file = scan.nextLine();
                    file = file.trim();
                    dictionaryImportFromFile(file);
                    wordListChange = true;
                    break;
                case 9:
                    dictionaryExportToFile();
                    break;
                default:
                    System.out.println("Action not supported");

            }
        }
    }
}