package com.dinuberinde.hooks;

import java.lang.annotation.*;

/**
 * Annotation used to mark the parameter of a target method when using {@link DataInHook}
 * <br/>
 * <p>Example:</p>
 *
 *<pre class="code">
 *  &#064;DataInHook(type = DataInHookSupplier.class, method = "dataIn")
 *  public void dataInExample(&#064;DataIn String input) {
 *      // param input is supplied by the hook method
 *  }
 *</pre>
 *
 *<p>Hook class and method:</p>
 <pre class="code">
 *public class DataInHookSupplier {
 *  public String dataIn(Hook hook) {
 *      return "this is supplied by the hook method";
 *  }
 *}
 *</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DataIn {
}
