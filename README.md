# Spring hooks
 
The goal of this project is to provide Spring based annotations to trigger hook methods for a target method.
A target method can be a REST endpoint or a traditional Java method.

## Features
* **PreHook** triggered **before** a target method
* **PostHook** triggered **after** a target method finished normally or with an exception
* **ExceptionHook** triggered **after** a target method throws an exception
* **DataInHook** triggered **before** a target method and supplies the target method with data returned by the hook 
* **DataOutHook** triggered **after** a target method finished normally without an exception and allows the hook to consume data returned by the target method
  
## Quickstart

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

## License

[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)

## Author
Dinu Berinde <dinu2193@gmail.com>
