package hooks;

import com.dinuberinde.hooks.Hook;
import helper.DataHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class LogHook {
    public static final String LOG_PRE = LogHook.class.getName() + "PRE";
    public static final String LOG_POST = LogHook.class.getName() + "POST";

    public void pre(Hook hook) {
        String tag = !hook.getTag().isEmpty() ? hook.getTag() : "mytag";

        // get the http request
        MockHttpServletRequest request = (MockHttpServletRequest) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String query = request.getParameter("query");

        DataHolder.map.put(LOG_PRE, new DataHolder.Logger(tag, query));
    }

    public void post(Hook hook) {
        String tag = !hook.getTag().isEmpty() ? hook.getTag() : "mytag";

        // increment the counter of the post method invocation
        DataHolder.Logger logger = DataHolder.map.computeIfAbsent(LOG_POST, k -> new DataHolder.Logger(tag, "0"));
        DataHolder.map.put(LOG_POST, new DataHolder.Logger(tag, (Integer.parseInt(logger.value) + 1) + ""));
    }
}
