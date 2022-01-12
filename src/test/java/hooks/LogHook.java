package hooks;

import helper.DataHolder;

public class LogHook {
    public static final String LOG_PRE = LogHook.class.getName() + "PRE";
    public static final String LOG_POST = LogHook.class.getName() + "POST";

    public void logPre(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent(LOG_PRE, k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put(LOG_PRE, new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }

    public void logPost(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent(LOG_POST, k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put(LOG_POST, new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }
}
