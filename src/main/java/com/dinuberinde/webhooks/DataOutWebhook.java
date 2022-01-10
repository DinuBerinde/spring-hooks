package com.dinuberinde.webhooks;

import java.lang.annotation.*;

/**
 * Annotation used to supply a webhook method with the result returned by the target method.<br/>
 * The webhook method will be invoked <strong>after</strong> the target method.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *  &#064;DataOutWebhook(type = DataOutWebhookConsumer.class, method = "dataOut", dataType = String.class)
 *  public String dataOutExample() {
 *      return "string passed to the webhook method datOut(String)";
 *  }
 *</pre>
 *
 *<p>Webhook class and method:</p>
 *<pre class="code">
 *public class DataOutWebhookConsumer {
 *  public void dataOut(String tag, String data) {
 *      // consume returned data of the target method
 *  }
 *}
 *</pre>
 *
 * <p>
 * The webhook method signature must be: <br/> <strong>{@code public T methodName(String, R)}</strong><br/>
 * If the webhook method does not get specified, the annotation assumes that
 * the webhook class has the following method defined: <br/> <strong>{@code public T dataOut(String, R)}</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataOutWebhook {

    /**
     * The class type of the webhook
     */
    Class<?> type();

    /**
     * The method name of the webhook
     */
    String method() default "dataOut";

    /**
     * The argument class type of the webhook method
     */
    Class<?> dataType();

    /**
     * The tag of the webhook
     */
    String tag() default "";
}
