package hooks;

import helper.DataHolder;

public class NoArgsLogHook {

    public void logNoArgs() {
        DataHolder.map.put(NoArgsLogHook.class.getName(), new DataHolder.Logger("notag", "hook with no args"));
    }
}
