package com.dinuberinde.hooks;

import java.lang.annotation.*;

/**
 * Annotation used to supply a target method with the result returned by the hook method.
 * It is used together with the {@link Data} annotation, which marks the parameter of the target method
 * that will be supplied.<br/>
 * The hook method will be invoked <strong>before</strong> the target method.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *  &#064;DataInHook(type = DataInHookSupplier.class, method = "dataIn", dataType = String.class)
 *  public void dataInExample(&#064;Data String input) { }
 *</pre>
 *
 *<p>Hook class and method:</p>
 <pre class="code">
 *public class DataInHookSupplier {
 *  public String dataIn(String tag) {
 *      return "this is supplied by the hook method";
 *  }
 *}
 *</pre>
 *
 * <p>
 * The hook method signature must be: <br/> <strong>{@code public T methodName(String)}</strong><br/>
 * If the hook method does not get specified, the annotation assumes that
 * the hook class has the following method defined: <br/> <strong>{@code public T dataIn(String)}</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataInHook {

    /**
     * The class type of the hook
     */
    Class<?> type();

    /**
     * The method name of the hook
     */
    String method() default "dataIn";

    /**
     * The return class type of the hook method
     */
    Class<?> dataType();

    /**
     * The tag of the hook
     */
    String tag() default "";
}
