package hooks;

import com.dinuberinde.hooks.Hook;
import helper.DataHolder;
import org.springframework.stereotype.Component;

@Component
public class LogHookComponent {

    public void log(Hook hook) {
        String tag_ = !hook.getTag().isEmpty() ? hook.getTag() : "mytag";

        // increment the counter of the log method invocation
        DataHolder.Logger logger = DataHolder.map.computeIfAbsent(LogHookComponent.class.getName(), k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put(LogHookComponent.class.getName(), new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }
}
