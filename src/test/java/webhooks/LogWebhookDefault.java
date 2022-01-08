package webhooks;

import helper.DataHolder;

public class LogWebhookDefault {

    public void post(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent("LogWebhookDefaultPOST", k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put("LogWebhookDefaultPOST", new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }

    public void pre(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent("LogWebhookDefaultPRE", k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put("LogWebhookDefaultPRE", new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }
}
