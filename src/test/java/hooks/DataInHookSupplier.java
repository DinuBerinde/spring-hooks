package hooks;

import helper.DataHolder;

public class DataInHookSupplier {

    public String dataIn(String tag) {
        DataHolder.map.put("DataInHookSupplier", new DataHolder.Logger(tag, "this is injected by the hook method"));
        return "this is injected by the hook method";
    }
}
