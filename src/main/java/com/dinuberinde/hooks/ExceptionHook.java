package com.dinuberinde.hooks;

import java.lang.annotation.*;

/**
 * Annotation used to access the exception thrown by a target method. <br/>
 * The hook method will be triggered <strong>after</strong> the target method throws an exception.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *  &#064;ExceptionHook(definingClass = LogException.class, method = "exception")
 *  public void exceptionExample() {
 *      String a = null;
 *      a.length();
 *  }
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
 * The hook method signature must be: <br/> <strong>{@code public T methodName(Hook)}</strong><br/>
 * If the hook method does not get specified, the annotation assumes that
 * the hook class has the following method defined: <br/><strong{@code public T exception(Hook)}</strong>
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
