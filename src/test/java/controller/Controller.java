package controller;


import com.dinuberinde.webhooks.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import webhooks.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class Controller {

    /**
     * Example of using a PRE and a POST webhook for an endpoint method.
     *
     * The logPre() webhook method of {@link LogWebhook} gets invoked BEFORE the hello() method.
     * The logPost() webhook method of {@link LogWebhook} gets invoked AFTER the hello() method.
     */
    @PreWebhook(type = LogWebhook.class, method = "logPre", tag = "/hello")
    @PostWebhook(type = LogWebhook.class, method = "logPost", tag = "/hello")
    @GetMapping(value = "hello")
    public String hello() {
        return "hello";
    }

    /**
     * Example of using multiple webhooks for a single endpoint method.
     *
     * The logPre(), pre() webhooks methods of {@link LogWebhook} and {@link LogWebhookDefault} are getting invoked BEFORE the helloMultipleWebhooks() method.
     * The logPost(), post() webhooks methods of {@link LogWebhook} and {@link LogWebhookDefault} are getting invoked AFTER the helloMultipleWebhooks() method.
     * If not specified in the annotation, the default method of a PostWebhook class is post() and pre() for a PreWebhook class.
     */
    @PostWebhook(type = {LogWebhook.class, LogWebhookDefault.class}, method = {"logPost"}, tag = "/hello-multiple-webhooks")
    @PreWebhook(type = {LogWebhook.class, LogWebhookDefault.class}, method = {"logPre", "pre"}, tag = "/hello-multiple-webhooks")
    @GetMapping(value = "hello-multiple-webhooks")
    public String helloMultipleWebhooks() {
        return "called multiple webhooks";
    }

    /**
     * Example of using a Spring bean class as a webhook. The behaviour is exactly the same as normal Java class.
     *
     * The log() webhook method of {@link LogWebhookComponent} gets invoked BEFORE the helloSpringComponent() method.
     */
    @PreWebhook(type = LogWebhookComponent.class, method = "log", tag = "hello-spring-component")
    @GetMapping(value = "hello-spring-component")
    public String helloSpringComponent() {
        return "hello spring component";
    }

    /**
     * Example of webhooks where the actual method definition is omitted hence the default methods of the webhooks are being called.
     *
     * The default method of {@link PreWebhook} class is pre()
     * The default method of {@link PostWebhook} class is post()
     * The default method of {@link DataOutWebhook} class is dataOut(<T>)
     * The default method of {@link DataInWebhook} class is dataIn()
     * The default method of {@link ExceptionWebhook} class is exception(Exception)
     */
    @PostWebhook(type = LogWebhookDefault.class)
    @PreWebhook(type = LogWebhookDefault.class)
    @GetMapping(value = "hello-default-webhook-methods")
    public String helloDefaultWebhookMethods() {
        return "hello default webhook methods";
    }

    /**
     * Example of a webhook method where we access the {@link HttpServletRequest} of the current request.
     * The method pre() of {@link LogHttpServletRequestWebhook} accesses the current request BEFORE the helloHttpServletRequest() method
     * gets invoked.
     */
    @PreWebhook(type = LogHttpServletRequestWebhook.class, tag = "/hello-http-servlet-request")
    @GetMapping(value = "hello-http-servlet-request")
    public String helloHttpServletRequest(String query) {
        return "hello http servlet request query " + query;
    }

    /**
     * Example of webhook logs the exception of the current endpoint method when the method throws an exception.
     * The post webhook will be invoked as well.
     *
     * The exception() webhook method of {@link LogException} gets invoked AFTER the exceptionExample() method
     * throws an exception.
     */
    @ExceptionWebhook(type = LogException.class, method = "exception")
    @PostWebhook(type = LogWebhook.class, method = "logPost", tag = "/exception")
    @GetMapping(value = "exception")
    public void exceptionExample() {
        String a = null;
        a.length();
    }

    /**
     * Example of a webhook that supplies data to the current endpoint method.
     *
     * The dataIn() webhook method of {@link DataInWebhookSupplier} gets invoked BEFORE the dataInExample() method
     * and the returned value of the webhook method gets binded to the parameter annotated with {@link Data} of dataInExample(), in this case dataValueInjectByWebhook.
     */
    @DataInWebhook(type = DataInWebhookSupplier.class, method = "dataIn", dataType = String.class)
    @GetMapping(value = "data-in")
    public String dataInExample(String value, @Data String dataValueInjectByWebhook) {
        return value + "@" + dataValueInjectByWebhook;
    }

    /**
     * Example of a webhook that consumes the returned value of the current endpoint method.
     *
     * The dataOut() webhook method of {@link DataOutWebhookConsumer} gets invoked AFTER the dataOutExample() method
     * and the returned value of dataOutExample() method gets binded to the parameter of the webhook dataOut() method.
     */
    @DataOutWebhook(type = DataOutWebhookConsumer.class, method = "dataOut", dataType = String.class)
    @GetMapping(value = "data-out")
    public String dataOutExample(String value) {
        return value;
    }
}
