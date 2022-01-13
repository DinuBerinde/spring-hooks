package com.dinuberinde.hooks;

import java.lang.annotation.*;

/**
 * Annotation used to trigger a hook method <strong>before</strong> the target method.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *  &#064;PreHook(definingClass = LogHook.class, method = "log", tag = "/hello")
 *  public void hello() { }
 *</pre>
 *
 *<p>Hook class and method:</p>
 *<pre class="code">
 *public class LogHook {
 *  public void log(Hook hook) {
 *      // log something
 *  }
 * }
 *</pre>
 *
 * <p>
 * The hook method signature must be: <br/> <strong>{@code public T methodName(Hook)}</strong><br/>
 * If the hook method does not get specified, the annotation assumes that
 * the hook class has the following method defined: <br/> <strong>{@code public T pre(Hook)}</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreHook {

    /**
     * The defining class of the hook
     */
    Class<?>[] definingClass();

    /**
     * The method name of the hook
     */
    String[] method() default {};

    /**
     * The tag of the hook
     */
    String tag() default "";
}
