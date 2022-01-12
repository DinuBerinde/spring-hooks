package hooks;

import helper.DataHolder;

public class DataOutHookConsumer {

    public void dataOut(String tag, String data) {
        DataHolder.map.put("DataOutHookConsumer", new DataHolder.Logger(tag, data));
    }
}
