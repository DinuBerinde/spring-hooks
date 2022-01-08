package webhooks;

import helper.DataHolder;

public class LogWebhook {

    public void logPre(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent("LogWebhookPRE", k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put("LogWebhookPRE", new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }

    public void logPost(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent("LogWebhookPOST", k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put("LogWebhookPOST", new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }
}
