package controller;


import com.dinuberinde.hooks.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import hooks.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class Controller {

    /**
     * Example of using a PRE and a POST hook for an endpoint method.
     *
     * The logPre() hook method of {@link LogHook} gets invoked BEFORE the hello() method.
     * The logPost() hook method of {@link LogHook} gets invoked AFTER the hello() method.
     */
    @PreHook(type = LogHook.class, method = "logPre", tag = "/hello")
    @PostHook(type = LogHook.class, method = "logPost", tag = "/hello")
    @GetMapping(value = "hello")
    public String hello() {
        return "hello";
    }

    /**
     * Example of using multiple hooks for a single endpoint method.
     *
     * The logPre(), pre() hooks methods of {@link LogHook} and {@link LogHookDefault} are getting invoked BEFORE the helloMultipleHooks() method.
     * The logPost(), post() hooks methods of {@link LogHook} and {@link LogHookDefault} are getting invoked AFTER the helloMultipleHooks() method.
     * If not specified in the annotation, the default method of a PostHook class is post() and pre() for a PreHook class.
     */
    @PostHook(type = {LogHook.class, LogHookDefault.class}, method = {"logPost"}, tag = "/hello-multiple-hooks")
    @PreHook(type = {LogHook.class, LogHookDefault.class}, method = {"logPre", "pre"}, tag = "/hello-multiple-hooks")
    @GetMapping(value = "hello-multiple-hooks")
    public String helloMultipleHooks() {
        return "called multiple hooks";
    }

    /**
     * Example of using a Spring bean class as a hook. The behaviour is exactly the same as normal Java class.
     *
     * The log() hook method of {@link LogHookComponent} gets invoked BEFORE the helloSpringComponent() method.
     */
    @PreHook(type = LogHookComponent.class, method = "log", tag = "hello-spring-component")
    @GetMapping(value = "hello-spring-component")
    public String helloSpringComponent() {
        return "hello spring component";
    }

    /**
     * Example of hooks where the actual method definition is omitted hence the default methods of the hooks are being called.
     *
     * The default method of {@link PreHook} class is pre()
     * The default method of {@link PostHook} class is post()
     * The default method of {@link DataInHook} class is dataOut(<T>)
     * The default method of {@link DataOutHook} class is dataIn()
     * The default method of {@link ExceptionHook} class is exception(Exception)
     */
    @PostHook(type = LogHookDefault.class)
    @PreHook(type = LogHookDefault.class)
    @GetMapping(value = "hello-default-hook-methods")
    public String helloDefaultHookMethods() {
        return "hello default hook methods";
    }

    /**
     * Example of a hook method where we access the {@link HttpServletRequest} of the current request.
     * The method pre() of {@link LogHttpServletRequestHook} accesses the current request BEFORE the helloHttpServletRequest() method
     * gets invoked.
     */
    @PreHook(type = LogHttpServletRequestHook.class, tag = "/hello-http-servlet-request")
    @GetMapping(value = "hello-http-servlet-request")
    public String helloHttpServletRequest(String query) {
        return "hello http servlet request query " + query;
    }

    /**
     * Example of hook logs the exception of the current endpoint method when the method throws an exception.
     * The post hook will be invoked as well.
     *
     * The exception() hook method of {@link LogException} gets invoked AFTER the exceptionExample() method
     * throws an exception.
     */
    @ExceptionHook(type = LogException.class, method = "exception")
    @PostHook(type = LogHook.class, method = "logPost", tag = "/exception")
    @GetMapping(value = "exception")
    public void exceptionExample() {
        String a = null;
        a.length();
    }

    /**
     * Example of a hook that supplies data to the current endpoint method.
     *
     * The dataIn() hook method of {@link DataInHookSupplier} gets invoked BEFORE the dataInExample() method
     * and the returned value of the hook method gets binded to the parameter annotated with {@link Data}
     * of dataInExample(), in this case dataValueSuppliedByHook.
     */
    @DataInHook(type = DataInHookSupplier.class, method = "dataIn", dataType = String.class)
    @GetMapping(value = "data-in")
    public String dataInExample(String value, @Data String dataValueSuppliedByHook) {
        return value + "@" + dataValueSuppliedByHook;
    }

    /**
     * Example of a hook that consumes the returned value of the current endpoint method.
     *
     * The dataOut() hook method of {@link DataOutHookConsumer} gets invoked AFTER the dataOutExample() method
     * and the returned value of dataOutExample() method gets binded to the parameter of the hook dataOut() method.
     */
    @DataOutHook(type = DataOutHookConsumer.class, method = "dataOut", dataType = String.class)
    @GetMapping(value = "data-out")
    public String dataOutExample(String value) {
        return value;
    }
}
