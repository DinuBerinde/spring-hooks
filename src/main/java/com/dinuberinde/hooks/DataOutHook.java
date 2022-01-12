package com.dinuberinde.hooks;

import java.lang.annotation.*;



/**
 * Annotation which allows the hook method to consume data returned by the target method.<br/>
 * The hook method will be triggered <strong>after</strong> the target method finished normally without an exception.
 * <br/>
 *<p>Example:</p>
 *<pre class="code">
 *  &#064;DataOutHook(type = DataOutHookConsumer.class, method = "dataOut", dataType = String.class)
 *  public String dataOutExample() {
 *      return "string passed to the hook method datOut(String, String)";
 *  }
 *</pre>
 *
 *<p>Hook class and method:</p>
 *<pre class="code">
 *public class DataOutHookConsumer {
 *  public void dataOut(String tag, String data) {
 *      // consume returned data of the target method
 *  }
 *}
 *</pre>
 *
 * <p>
 * The hook method signature must be: <br/> <strong>{@code public T methodName(String, R)}</strong><br/>
 * If the hook method does not get specified, the annotation assumes that
 * the hook class has the following method defined: <br/> <strong>{@code public T dataOut(String, R)}</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataOutHook {

    /**
     * The class type of the hook
     */
    Class<?> type();

    /**
     * The method name of the hook
     */
    String method() default "dataOut";

    /**
     * The argument class type of the hook method
     */
    Class<?> dataType();

    /**
     * The tag of the hook
     */
    String tag() default "";
}
