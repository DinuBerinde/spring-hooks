package helper;

import java.util.HashMap;
import java.util.Map;

public class DataHolder {
    public static final Map<String, Logger> map = new HashMap<>();

    public static class Logger {
        public final String tag;
        public final String value;

        public Logger(String tag, String value) {
            this.tag = tag;
            this.value = value;
        }
    }
}
