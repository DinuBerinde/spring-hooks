package com.dinuberinde.webhooks;

import java.lang.annotation.*;

/**
 * Annotation used to supply a target method with the result returned by the webhook method.
 * It is used together with the {@link Data} annotation, which marks the parameter of the target method
 * that will be supplied.<br/>
 * The webhook method will be invoked <strong>before</strong> the target method.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *  &#064;DataInWebhook(type = DataInWebhookSupplier.class, method = "dataIn", dataType = String.class)
 *  public void dataInExample(&#064;Data String input) { }
 *</pre>
 *
 *<p>Webhook class and method:</p>
 <pre class="code">
 *public class DataInWebhookSupplier {
 *  public String dataIn(String tag) {
 *      return "this is supplied by the webhook method";
 *  }
 *}
 *</pre>
 *
 * <p>
 * The webhook method signature must be: <br/> <strong>{@code public T methodName(String)}</strong><br/>
 * If the webhook method does not get specified, the annotation assumes that
 * the webhook class has the following method defined: <br/> <strong>{@code public T dataIn(String)}</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataInWebhook {

    /**
     * The class type of the webhook
     */
    Class<?> type();

    /**
     * The method name of the webhook
     */
    String method() default "dataIn";

    /**
     * The return class type of the webhook method
     */
    Class<?> dataType();

    /**
     * The tag of the webhook
     */
    String tag() default "";
}
