package com.dinuberinde.webhooks;

import java.lang.annotation.*;

/**
 * Annotation used to mark the parameter of a target method when using {@link DataInWebhook}
 * <br/>
 * <p>Example:</p>
 *
 *<pre class="code">
 *  &#064;DataInWebhook(type = DataInWebhookSupplier.class, method = "dataIn", dataType = String.class)
 *  public void dataInExample(&#064;Data String input) { }
 *</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Data {
}
