package hooks;

import helper.DataHolder;

public class DataOutHookConsumer {

    public void dataOut(String tag, String data) {
        DataHolder.map.put(DataOutHookConsumer.class.getName(), new DataHolder.Logger(tag, data));
    }
}
