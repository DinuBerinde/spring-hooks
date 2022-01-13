package com.dinuberinde.hooks;

import java.lang.annotation.*;

/**
 * Annotation which supplies a target method with the result returned by the hook method.
 * It is used together with the {@link Data} annotation, which marks the parameter of the target method
 * that will be supplied.<br/>The return type of the hook method must match the type of the parameter annotated
 * with the {@link Data} annotation.<br/>
 * The hook method will be triggered <strong>before</strong> the target method.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *  &#064;DataInHook(definingClass = DataInHookSupplier.class, method="dataIn")
 *  public void dataInExample(&#064;Data String input) {
 *      System.out.println(input); // prints: this is supplied by the hook method
 *   }
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
 * The hook method signature must be: <br/> <strong>{@code public T methodName(Hook)}</strong><br/>
 * If the hook method does not get specified, the annotation assumes that
 * the hook class has the following method defined: <br/> <strong>{@code public T dataIn(Hook)}</strong>
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
