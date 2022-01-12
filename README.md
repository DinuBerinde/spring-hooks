# Spring hooks
 
The goal of this project is to provide Spring based annotations to trigger the hook methods for a target method.
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

    @PreHook(type = LogHook.class, tag = "/hello")
    @PostHook(type = LogHook.class, tag = "/hello")
    @GetMapping(value = "hello")
    public void helloEndpoint() {
      System.out.println("doing something inside /hello endpoint");
    }
}

// Hook class and methods
public class LogHook {

    public void pre(String tag) {
        System.out.println("pre hook called for " + tag);
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        // do something with the request
    }

    public void post(String tag) {
        System.out.println("post hook called for " + tag);    
    }
}

```

## License

[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)

## Author
Dinu Berinde <dinu2193@gmail.com>
