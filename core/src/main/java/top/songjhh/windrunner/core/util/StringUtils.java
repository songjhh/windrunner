package top.songjhh.windrunner.core.util;

/**
 * @author songjhh
 */
public class StringUtils {

    private static final String EMPTY_STR = "";

    private StringUtils() {
    }

    public static boolean isEmpty(String s) {
        return s == null || EMPTY_STR.equals(s);
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

}
