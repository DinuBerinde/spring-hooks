package com.dinuberinde.webhooks;

import java.lang.annotation.*;

/**
 * Annotation used to invoke a webhook method <strong>before</strong> the target method.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *  &#064;PreWebhook(type = LogWebhook.class, method = "logPre", tag = "/hello")
 *  public void hello() { }
 *</pre>
 *
 *<p>Webhook class and method:</p>
 *<pre class="code">
 *public class LogWebhook {
 *  public void logPre(String tag) {
 *      // log tag
 *  }
 * }
 *</pre>
 *
 * <p>
 * The webhook method signature must be: <br/> <strong>{@code public T methodName(String)}</strong><br/>
 * If the webhook method does not get specified, the annotation assumes that
 * the webhook class has the following method defined: <br/> <strong>{@code public T pre(String)}</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreWebhook {

    /**
     * The class type of the webhook
     */
    Class<?>[] type();

    /**
     * The method name of the webhook
     */
    String[] method() default {};

    /**
     * The tag of the webhook
     */
    String tag() default "";
}
