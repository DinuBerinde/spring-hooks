package webhooks;

import helper.DataHolder;
import org.springframework.stereotype.Component;

@Component
public class LogWebhookComponent {

    public void log(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent("LogWebhookComponent", k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put("LogWebhookComponent", new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }
}
