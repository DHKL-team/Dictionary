import java.io.*;
import java.util.Scanner;
import java.util.*;

public class DictionaryManagement {
    Dictionary dictionary = new Dictionary();
    public void insertFromFile(String file) throws FileNotFoundException {
        File f1 = new File(file);
        Scanner scan = new Scanner(f1);
        while (scan.hasNext()) {
            String target = scan.next();
            target = target.substring(0, 1).toLowerCase() + target.substring(1);
            String explain = scan.nextLine();
            explain = explain.trim();
            explain = explain.substring(0, 1).toLowerCase() + explain.substring(1);
            dictionary.insert(target, explain);
        }
    }

    public String dictionaryLookup(String target) {
        return dictionary.search(target).getWord_explain();
    }
    public Dictionary getDictionary() {
        return dictionary;
    }

    public void addData(String target, String explain) {
        target = target.substring(0, 1).toLowerCase() + target.substring(1);
        explain = explain.substring(0, 1).toLowerCase() + explain.substring(1);
        dictionary.insert(target, explain);
    }
    public void updateData(String target, String explain) {
        if (dictionary.search(target) != null) {
            dictionary.search(target).setWord_explain(explain);
        }
    }
    public Word removeData(String target) {
        return dictionary.remove(target);
    }

}
