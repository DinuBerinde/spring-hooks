package hooks;

import helper.DataHolder;

public class DataInHookSupplier {

    public String dataIn(String tag) {
        DataHolder.map.put(DataInHookSupplier.class.getName(), new DataHolder.Logger(tag, "this is supplied by the hook method"));
        return "this is supplied by the hook method";
    }
}
