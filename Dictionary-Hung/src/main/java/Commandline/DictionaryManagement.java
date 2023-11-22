package Commandline;


import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryManagement {

    private DictionaryManagement() {

    }

    public static DictionaryManagement getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final DictionaryManagement INSTANCE = new DictionaryManagement();
    }

    Dictionary dictionary = Dictionary.getInstance();
    public void insertFromFile(String file) throws FileNotFoundException {
        File f1 = new File(file);
        Scanner scan = new Scanner(f1);
        Word word = null;
        String regex = "@(.+)( /.+/)";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        while (scan.hasNext()) {
            String str = scan.nextLine();
            str = str.toLowerCase();
            Matcher m = p.matcher(str);
            if (m.find()) {
                if (word != null)
                    dictionary.insert(word);
                word = new Word(m.group(1), m.group(2), "");
            } else {
                if (word != null)
                    word.addWord_explain(str + '\n');
            }
        }
        if (word != null)
            dictionary.insert(word);
    }

    public String dictionaryLookup(String target) {
        String result = "";
        if (dictionary.search(target) != null) {
            result += dictionary.search(target).getWord_pronunciation() + '\n' + dictionary.search(target).getWord_explain();
        }
        return result;
    }
    public Dictionary getDictionary() {
        return dictionary;
    }

    public void addData(String target, String explain) {
        dictionary.insert(target, explain);
    }

    public void addData(String target, String pronunciation, String explain) {
        dictionary.insert(new Word(target, pronunciation, explain));
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