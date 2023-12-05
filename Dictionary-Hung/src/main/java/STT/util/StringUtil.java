package STT.util;

public class StringUtil {
	
	private StringUtil() {};//Prevents instantiation

    public static String stripQuotes(String s) {
        int start = 0;
        if( s.startsWith("\"") ) {
            start = 1;
        }
        int end = s.length();
        if( s.endsWith("\"") ) {
            end = s.length() - 1;
        }
        return s.substring(start, end);
    }

    public static String substringBetween(String s, String part1, String part2) {
        String sub = null;

        int i = s.indexOf(part1);
        int j = s.indexOf(part2, i + part1.length());

        if (i != -1 && j != -1) {
            int nStart = i + part1.length();
            sub = s.substring(nStart, j);
        }

        return sub;
    }

	public static String trimString(String s, String part1, String part2){
		if(!s.contains(part1) || !s.contains(part2)){
			return null;
		}
		int first = s.indexOf(part1) + part1.length() + 1;
		String tmp = s.substring(first);
		int last = tmp.lastIndexOf(part2);
		tmp = tmp.substring(0, last);
		return tmp;
	}

}
