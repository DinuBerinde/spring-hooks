package hooks;

import helper.DataHolder;

public class LogHookDefault {
    public static final String LOG_PRE = LogHookDefault.class.getName() + "PRE";
    public static final String LOG_POST = LogHookDefault.class.getName() + "POST";

    public void post(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent(LOG_POST, k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put(LOG_POST, new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }

    public void pre(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";
        System.out.println("debug");
        DataHolder.Logger logger = DataHolder.map.computeIfAbsent(LOG_PRE, k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put(LOG_PRE, new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }
}
