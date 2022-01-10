package webhooks;

import helper.DataHolder;

public class DataOutWebhookConsumer {

    public void dataOut(String tag, String data) {
        DataHolder.map.put("DataOutWebhookConsumer", new DataHolder.Logger(tag, data));
    }
}
