package webhooks;

import helper.DataHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class LogHttpServletRequestWebhook {

    public void pre(String tag) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        DataHolder.map.put("LogHttpServletRequestWebhook", new DataHolder.Logger(tag, request.getQueryString()));
    }
}
