package hooks;

import com.dinuberinde.hooks.Hook;
import com.fasterxml.jackson.databind.ObjectMapper;
import helper.Person;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;

public class PersonDataInHook {

    public Person dataIn(Hook hook) throws UnsupportedEncodingException {
        // get the http request
        MockHttpServletRequest request = (MockHttpServletRequest) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // enrich the person with an id
        Person person = fromJson(request.getContentAsString(), Person.class);
        person.setId(20);

        return person;
    }

    private static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
