package com.dinuberinde.webhooks;

import java.lang.annotation.*;


/**
 * Annotation used to invoke a webhook method <strong>after</strong> the target method finished normally or with an exception.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *  &#064;PostWebhook(type = LogWebhook.class, method = "logPost", tag = "/hello")
 *  public void hello() { }
 *</pre>
 *
 *<p>Webhook class and method:</p>
 *<pre class="code">
 *public class LogWebhook {
 *  public void logPost(String tag) {
 *      // log tag
 *  }
 * }
 *</pre>
 *
 * <p>
 * The webhook method signature must be: <br/> <strong>{@code public T methodName(String)}</strong><br/>
 * If the webhook method does not get specified, the annotation assumes that
 * the webhook class has the following method defined: <br/> <strong>{@code public T post(String)}</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostWebhook {

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

