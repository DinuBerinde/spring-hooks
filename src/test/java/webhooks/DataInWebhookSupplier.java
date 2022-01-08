package webhooks;

import helper.DataHolder;

public class DataInWebhookSupplier {

    public String dataIn(String tag) {
        DataHolder.map.put("DataInWebhookSupplier", new DataHolder.Logger(tag, "this is injected by the webhook method"));
        return "this is injected by the webhook method";
    }
}
