package com.dinuberinde.hooks;

import java.lang.annotation.*;

/**
 * Annotation used to access the exception thrown by a target method. <br/><br/>
 * The hook method will be triggered <strong>after</strong> the target method throws an exception.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *&#064;ExceptionHook(definingClass = LogException.class, method = "exception")
 *public void exceptionExample() {
 *  String a = null;
 *  a.length();
 *}
 *</pre>
 *
 *<p>Hook class and method:</p>
 *<pre class="code">
 *public class LogException {
 *  public void exception(Hook hook) {
 *      // handle exception
 *      Exception exception = hook.getException();
 *  }
 * }
 *</pre>
 *
 * <p>
 * The hook method must be {@code public} and accepts {@link Hook} as an optional parameter.
 * The default name of the hook method is <strong>exception</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionHook {

    /**
     * The defining class of the hook
     */
    Class<?> definingClass();

    /**
     * The method name of the hook. It defaults to {@code exception}
     */
    String method() default "exception";

    /**
     * The tag of the hook
     */
    String tag() default "";
}
