package com.dinuberinde.webhooks;

import java.lang.annotation.*;

/**
 * Annotation used to access the exception thrown by a target method. <br/>
 * The webhook method will be invoked <strong>after</strong> the target method throws an exception.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *  &#064;ExceptionWebhook(type = LogException.class, method = "exception")
 *  public void exceptionExample() { }
 *</pre>
 *
 *<p>Webhook class and method:</p>
 *<pre class="code">
 *public class LogException {
 *  public void exception(String tag, Exception exception) {
 *      // access exception
 *  }
 * }
 *</pre>
 *
 * <p>
 * The webhook method signature must be: <br/> <strong>{@code public T methodName(String, Exception)}</strong><br/>
 * If the webhook method does not get specified, the annotation assumes that
 * the webhook class has the following method defined: <br/><strong{@code public T exception(String, Exception)}</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionWebhook {

    /**
     * The class type of the webhook
     */
    Class<?> type();

    /**
     * The method name of the webhook. It defaults to {@code exception}
     */
    String method() default "exception";

    /**
     * The tag of the webhook
     */
    String tag() default "";
}
