package com.dinuberinde.hooks;

import java.lang.annotation.*;

/**
 * Annotation which supplies a target method with the result returned by the hook method.
 * It is used together with the {@link DataIn} annotation, which marks the parameter of the target method
 * that will be supplied with data. The return type of the hook method must match the type of the parameter annotated
 * with the {@link DataIn} annotation.<br><br>
 * The hook method will be triggered <strong>before</strong> the target method.
 * <br>
 *<p>Example:</p>
 *<pre class="code">
 *&#064;DataInHook(definingClass = DataInHookSupplier.class, method="dataIn")
 *public void dataInExample(&#064;DataIn String input) {
 *  System.out.println(input); // prints: this is supplied by the hook method
 *}
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
 *
 * <p>
 * The hook method must be {@code public} and accepts {@link Hook} as an optional parameter.
 * The default name of the hook method is <strong>dataIn</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataInHook {

    /**
     * The defining class of the hook
     */
    Class<?> definingClass();

    /**
     * The method name of the hook
     */
    String method() default "dataIn";

    /**
     * The tag of the hook
     */
    String tag() default "";
}
