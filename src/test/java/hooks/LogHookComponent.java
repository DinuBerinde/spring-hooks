package hooks;

import helper.DataHolder;
import org.springframework.stereotype.Component;

@Component
public class LogHookComponent {

    public void log(String tag) {
        final String tag_ = !tag.isEmpty() ? tag : "mytag";

        DataHolder.Logger logger = DataHolder.map.computeIfAbsent(LogHookComponent.class.getName(), k -> new DataHolder.Logger(tag_, "0"));
        DataHolder.map.put(LogHookComponent.class.getName(), new DataHolder.Logger(tag_, (Integer.parseInt(logger.value) + 1) + ""));
    }
}
