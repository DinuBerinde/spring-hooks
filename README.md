# Spring hooks
 
The goal of this project is to provide Spring based annotations to trigger hook methods for a target method.
A target method can be a REST endpoint or a traditional Java method.

## Features
* **@PreHook** triggered **before** a target method
* **@PostHook** triggered **after** a target method finished normally or with an exception
* **@ExceptionHook** triggered **after** a target method throws an exception
* **@DataInHook** triggered **before** a target method and supplies the target method with data returned by the hook 
* **@DataOutHook** triggered **after** a target method finished normally without an exception and allows the hook to consume data returned by the target method
  
## Quickstart

#### @PreHook and @PostHook

```java
@RestController
public class Controller {

    @PreHook(definingClass = LogHook.class, tag = "/hello")
    @PostHook(definingClass = LogHook.class, tag = "/hello")
    @GetMapping(value = "hello")
    public void helloEndpoint() {
        ...
    }
}

// Hook class and methods
public class LogHook {

    public void pre(Hook hook) {
        System.out.println("pre hook called for " + hook.getTag());
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        // do something with the request
    }

    public void post(Hook hook) {
        System.out.println("post hook called for " + hook.getTag());    
    }
}
```

#### @DataInHook and @DataOutHook

```java
@RestController
public class Controller {

    @DataInHook(definingClass = DataInHookSupplier.class)
    @GetMapping(value = "hello-data-in")
    public void helloDataIn(@DataIn String data) {
        System.out.println(input); // prints: this is supplied by the hook method
    }

    @DataOutHook(definingClass = DataOutHookConsumer.class)
    @GetMapping(value = "hello-data-out")
    public String helloDataOut() {
       return "string passed to the hook method";
    }
}

// Hook classes and methods
public class DataInHookSupplier {

    public String dataIn(Hook hook) {
        return "this is supplied by the hook method";
    }
}

public class DataOutHookConsumer {

    public void dataOut(Hook hook) {
        // consume returned data of the target method
        String data = (String) hook.getDataOut();
        System.out.println(data); // prints: string passed to the hook method
    }
}
```
#### @ExceptionHook
```java
@RestController
public class Controller {

    @ExceptionHook(definingClass = LogException.class, tag = "/hello-exception")
    @GetMapping(value = "hello-exception")
    public void helloException() {
        throw new IllegalStateException("exception example");
    }
}

// Hook class and methods
public class LogException {
    
    public void exception(Hook hook) {
        // handle exception
        Exception exception = hook.getException();
        System.out.println(exception.getMessage()); // prints: exception example
    }
}
```
## Maven

```xml
<dependency>
    <groupId>com.dinuberinde</groupId>
    <artifactId>spring-hooks</artifactId>
    <version>1.0</version>
</dependency>
```

## License

[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)

## Author
Dinu Berinde <dinu2193@gmail.com>
