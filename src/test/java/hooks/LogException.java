package hooks;

import com.dinuberinde.hooks.Hook;
import helper.DataHolder;

public class LogException {

    public void exception(Hook hook) {
        DataHolder.map.put(LogException.class.getName(), new DataHolder.Logger(hook.getTag(), hook.getException().getClass().getName()));
    }
}
