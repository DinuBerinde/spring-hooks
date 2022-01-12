package hooks;

import helper.DataHolder;

public class LogException {

    public void exception(String tag, Exception exception) {
        final String tag_ = !tag.isEmpty() ? tag : "myexceptiontag";

        DataHolder.map.put(LogException.class.getName(), new DataHolder.Logger(tag_, exception.getClass().getName()));
    }
}
