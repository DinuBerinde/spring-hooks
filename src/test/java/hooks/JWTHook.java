package hooks;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

public class JWTHook {

    public void secure() {
        MockHttpServletRequest request = (MockHttpServletRequest) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String jwtHeader = request.getHeader("jwt");

        if (jwtHeader == null || !jwtHeader.equals("abcd")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allowed to access api");
        }
    }
}
