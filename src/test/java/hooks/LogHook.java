package hooks;

import helper.DataHolder;

public class LogHook {

    public void logPre(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent("LogHookPRE", k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put("LogHookPRE", new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }

    public void logPost(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent("LogHookPOST", k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put("LogHookPOST", new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }
}
