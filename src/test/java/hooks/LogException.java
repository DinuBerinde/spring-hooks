package hooks;

import com.dinuberinde.hooks.Hook;
import helper.DataHolder;

public class LogException {

    public void exception(Hook hook) {
        String exceptionToLog = hook.getException().getClass().getName() + ":" + hook.getException().getMessage();
        DataHolder.map.put(LogException.class.getName(), new DataHolder.Logger(hook.getTag(), exceptionToLog));
    }
}
