package com.dinuberinde.hooks;

import java.lang.annotation.*;

/**
 * Annotation used to mark the parameter of a target method when using {@link DataInHook}
 * <br/>
 * <p>Example:</p>
 *
 *<pre class="code">
 *  &#064;DataInHook(type = DataInHookSupplier.class, method = "dataIn", dataType = String.class)
 *  public void dataInExample(&#064;Data String input) {
 *      // param input is supplied by the hook method
 *  }
 *</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Data {
}
