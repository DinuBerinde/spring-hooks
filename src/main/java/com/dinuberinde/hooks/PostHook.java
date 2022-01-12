package com.dinuberinde.hooks;

import java.lang.annotation.*;


/**
 * Annotation used to invoke a hook method <strong>after</strong> the target method finished normally or with an exception.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *  &#064;PostHook(type = LogHook.class, method = "logPost", tag = "/hello")
 *  public void hello() { }
 *</pre>
 *
 *<p>Hook class and method:</p>
 *<pre class="code">
 *public class LogHook {
 *  public void logPost(String tag) {
 *      // log tag
 *  }
 * }
 *</pre>
 *
 * <p>
 * The hook method signature must be: <br/> <strong>{@code public T methodName(String)}</strong><br/>
 * If the hook method does not get specified, the annotation assumes that
 * the hook class has the following method defined: <br/> <strong>{@code public T post(String)}</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostHook {

    /**
     * The class type of the hook
     */
    Class<?>[] type();

    /**
     * The method name of the hook
     */
    String[] method() default {};

    /**
     * The tag of the hook
     */
    String tag() default "";
}

