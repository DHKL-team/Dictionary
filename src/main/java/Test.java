import java.io.FileNotFoundException;
import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        DictionaryCommandLine dictionaryCommandLine = new DictionaryCommandLine();
        dictionaryCommandLine.dictionaryBasic();
        dictionaryCommandLine.dictionaryAdvanced();
    }
}
