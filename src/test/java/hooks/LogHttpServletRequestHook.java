package hooks;

import helper.DataHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class LogHttpServletRequestHook {

    public void pre(String tag) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        DataHolder.map.put(LogHttpServletRequestHook.class.getName(), new DataHolder.Logger(tag, request.getQueryString()));
    }
}
