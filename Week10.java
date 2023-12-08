import java.util.ArrayList;
import java.util.List;

public class Week10 {

    private String packages = "";
    private final List<String> libraries = new ArrayList<String>();
    private final List<String> classes = new ArrayList<String>();
    private final List<String> staticMethods = new ArrayList<String>();

    /**
     * Get all function.
     *
     * @param fileContent file content
     * @return list
     */
    public List<String> getAllFunctions(String fileContent) {
        String temp = removeAllComments(convertFileContentToLine(fileContent));
        String str = temp.replace("{", "{\n");
        String[] lines = str.split("\n");
        List<String> list = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("package")) {
                handlePackage(line);
            } else if (line.startsWith("import")) {
                handleLibraries(line);
            } else if (line.contains("class")) {
                handleClass(line);
            } else if (line.contains("interface")) {
                handleClass(line.replace("interface", "class"));
            } else if (line.contains("static")) {
                list.add(line);
            } else if (line.contains("enum")) {
                handleClass(line.replace("enum", "class"));
            }
        }
        for (String method : list) {
            handleStaticMethod(method);
        }
        return staticMethods;
    }

    /**
     * handle package.
     *
     * @param str string
     */
    private void handlePackage(String str) {
        String[] list = str.trim().split(" ");
        packages = list[list.length - 1].replace(";", "");
    }

    /**
     * handle libraries.
     *
     * @param str string
     */
    private void handleLibraries(String str) {
        String[] list = str.trim().split(" ");
        libraries.add(list[list.length - 1].replace(";", ""));
    }

    /**
     * handle class.
     *
     * @param line string
     */
    private void handleClass(String line) {
        int idx = line.indexOf("class ") + 6;
        int idx1 = line.indexOf(" ", idx);
        if (idx1 == -1) {
            return;
        }
        String name = line.substring(idx, idx1).trim();
        String[] string = name.split("<");
        classes.add(packages + "." + string[0]);
    }

    /**
     * handle parameters.
     *
     * @param str string
     * @return string
     */
    private String handleParameters(String str) {
        if (str.contains("...")) {
            return fixLibrariesString(str.replace("...", ""));
        }
        if (str.contains("[]")) {
            int idx = str.indexOf("[]");
            return fixLibrariesString(str.replace("[]", "")) + str.substring(idx);
        }
        if (!str.contains("<")) {
            return fixLibrariesString(str);
        }
        int idx = str.indexOf("<");
        String fix = fixLibrariesString(str.substring(0, idx)) + "<";
        String[] list = str.substring(idx + 1, str.length() - 1).split(", ");
        for (int i = 0; i < list.length; i++) {
            if (i != 0) {
                fix = fix.concat(",");
            }
            if (list[i].startsWith("?")) {
                fix = fix.concat(list[i]);
            } else {
                fix = fix + fixLibrariesString(list[i]);
            }
        }
        return fix.concat(">");
    }

    /**
     * handle static method.
     *
     * @param str string
     */
    private void handleStaticMethod(String str) {
        if (str.contains("=")) {
            return;
        }
        int idx = str.indexOf("(");
        int idx1 = str.indexOf(")", idx + 1);
        if (idx1 == -1 || idx == -1) {
            return;
        }
        String methodName = str.substring(str.substring(0, idx).lastIndexOf(" ") + 1, idx);
        if (idx1 - idx == 1) {
            staticMethods.add(methodName.concat("()"));
            return;
        }
        String tmp = methodName + "(";
        String params = str.substring(idx + 1, idx1);
        String[] list = params.split(", ");
        for (int i = 0; i < list.length; i++) {
            if (i != 0) {
                tmp = tmp.concat(",");
            }
            String str1 = list[i].substring(0, list[i].lastIndexOf(" "))
                    .replace("final ", "").trim();
            String param = handleParameters(str1);
            tmp = tmp.concat(param);
        }
        tmp = tmp.concat(")");
        staticMethods.add(tmp);
    }

    /**
     * fix library name.
     *
     * @param str string
     * @return string
     */
    private String fixLibrariesString(String str) {
        for (String s : libraries) {
            if (s.endsWith("." + str)) {
                return s;
            }
        }

        for (String s : classes) {
            if (s.endsWith(str)) {
                return s;
            }
        }

        if (str.charAt(0) <= 'Z' && str.charAt(0) >= 'A' && str.length() > 1) {
            return "java.lang." + str;
        }
        return str;
    }

    /**
     * convert file content to line.
     *
     * @param fileContent string
     * @return string
     */
    private String convertFileContentToLine(String fileContent) {
        StringBuilder sb = new StringBuilder(fileContent);
        int index = sb.indexOf("(");
        while (index != -1) {
            int index2 = sb.indexOf(")", index);
            for (int i = index2; i > index; i--) {
                if (sb.charAt(i) == '\n') {
                    sb.deleteCharAt(i);
                }
            }
            index = sb.indexOf("(", index + 2);
        }
        return sb.toString();
    }

    /**
     * remove all comments.
     *
     * @param fileContent string
     * @return string
     */
    private String removeAllComments(String fileContent) {
        StringBuilder sb = new StringBuilder(fileContent);
        int idx = sb.indexOf("//");
        while (idx != -1) {
            int idx1 = sb.indexOf("\n", idx);
            if (idx1 == -1) {
                sb.delete(idx, sb.length());
                break;
            }
            sb.delete(idx, idx1);
            idx = sb.indexOf("//");
        }

        idx = sb.indexOf("/*");
        while (idx != -1) {
            int idx1 = sb.indexOf("*/", idx);
            if (idx1 == -1) {
                break;
            }
            sb.delete(idx, idx1 + 3);
            idx = sb.indexOf("/*");
        }
        return sb.toString();
    }
} 