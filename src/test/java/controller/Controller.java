package controller;


import com.dinuberinde.hooks.*;
import helper.Person;
import hooks.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    /**
     * Example of using a {@link PreHook} and a {@link PostHook} for an endpoint method.
     * The {@link LogHook} class contains a pre and a post method that just logs the current request.
     * The pre hook method gets triggered before the current method and the post hook method gets triggered
     * after the current method.
     */
    @PreHook(definingClass = LogHook.class, tag = "/hello")
    @PostHook(definingClass = LogHook.class, tag = "/hello")
    @GetMapping(value = "hello")
    public void hello(String query) {}

    /**
     * Example of using multiple hooks for a single endpoint method.
     * You can use Spring beans too, for example {@link LogHookComponent}.
     * If not specified in the annotation, the default method name of a PostHook is post and pre for a PreHook.
     */
    @PostHook(definingClass = {LogHookComponent.class, NoArgsLogHook.class, LogHook.class}, method = {"log", "logNoArgs"}, tag = "/hello-multiple-hooks")
    @PreHook(definingClass = {LogHookComponent.class, LogHook.class}, method = {"log"}, tag = "/hello-multiple-hooks")
    @GetMapping(value = "hello-multiple-hooks")
    public void helloMultipleHooks(String query) {}

    /**
     * Example of a {@link ExceptionHook} that logs the exception of the current method when the method throws an exception.
     * The post hook will be invoked as well.
     */
    @ExceptionHook(definingClass = LogException.class, tag = "/exception")
    @PostHook(definingClass = LogHook.class, tag = "/exception")
    @GetMapping(value = "exception")
    public void exceptionExample() {
        throw new IllegalStateException("exception example");
    }

    /**
     * Example of {@link DataInHook} that enriches a person object with an ID and supplies the
     * enriched person to the current method.
     *
     * Furthermore, the {@link DataOutHook} consumes the returned value of the current value.
     *
     * @param person the person to be enriched with an id
     * @return the person created
     */
    @DataInHook(definingClass = PersonDataInHook.class)
    @DataOutHook(definingClass = PersonDataOutHook.class)
    @PostMapping(value = "create-person", produces = MediaType.APPLICATION_JSON_VALUE)
    public Person createPerson(@DataIn @RequestBody Person person) {
        return person;
    }


    /**
     * Example of using {@link PreHook} as a JWT filter to filter access to a rest endpoint.
     */
    @GetMapping(value = "security-example")
    @PreHook(definingClass = JWTHook.class, method = "secure")
    public String securityExample(String query) {
        return "rest api called " + query ;
    }
}
