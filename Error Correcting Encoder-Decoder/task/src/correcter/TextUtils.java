package correcter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TextUtils {

    public static String multiplySymbols(String text, int multiplyBy) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            for (int j = 0; j < multiplyBy; j++) {
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }

    public static String decodeString(String text, int range) {

        StringBuilder result = new StringBuilder();
        Map<Character, Integer> entries;

        for (int i = 0; i < text.length(); i += range) {

            entries =  new HashMap<>();

            for (int j = 0; j < range; j++) {
                entries.merge(text.charAt(j + i), 1, (v, vv) -> v + 1);
            }

            result.append(entries
                    .entrySet()
                    .stream()
                    .max((Comparator.comparingInt(e -> e.getValue())))
                    .get()
                    .getKey());
        }

        return result.toString();
    }
}
