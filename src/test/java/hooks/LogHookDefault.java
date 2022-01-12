package hooks;

import helper.DataHolder;

public class LogHookDefault {

    public void post(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent("LogHookDefaultPOST", k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put("LogHookDefaultPOST", new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }

    public void pre(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";
        System.out.println("debug");
        DataHolder.Logger logger = DataHolder.map.computeIfAbsent("LogHookDefaultPRE", k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put("LogHookDefaultPRE", new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }
}
