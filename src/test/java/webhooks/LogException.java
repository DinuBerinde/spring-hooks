package webhooks;

import helper.DataHolder;

public class LogException {

    public void exception(String tag, Exception exception) {
        final String tag_ = !tag.isEmpty() ? tag : "myexceptiontag";

        DataHolder.map.put("Exception", new DataHolder.Logger(tag_, exception.getClass().getName()));
    }
}
