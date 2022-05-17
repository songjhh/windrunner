package top.songjhh.windrunner.core.util;

import java.io.*;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author songjhh
 */
public class TestFileUtils {

    private TestFileUtils() {
    }

    public static String getString(String fileName) {
        String path =
                Objects.requireNonNull(TestFileUtils.class.getResource("/")).getPath()
                        + "demo" + File.separator + fileName;
        String jsonStr;
        File jsonFile = new File(path);
        try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), UTF_8)) {
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException ignored) {
            return null;
        }
    }

}
