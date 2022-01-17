package com.dinuberinde.hooks;

import java.lang.annotation.*;


/**
 * Annotation which allows the hook method to consume data returned by the target method.<br><br>
 * The hook method will be triggered <strong>after</strong> the target method finished normally without an exception.
 * <br>
 *<p>Example:</p>
 *<pre class="code">
 *&#064;DataOutHook(definingClass = DataOutHookConsumer.class, method = "dataOut")
 *public String dataOutExample() {
 *  return "string passed to the hook method";
 *}
 *</pre>
 *
 *<p>Hook class and method:</p>
 *<pre class="code">
 *public class DataOutHookConsumer {
 *  public void dataOut(Hook hook) {
 *       // consume returned data of the target method
 *       String data = (String) hook.getDataOut();
 *       System.out.println(data); // prints: string passed to the hook method
 *  }
 *}
 *</pre>
 *
 * <p>
 * The hook method must be {@code public} and accepts {@link Hook} as an optional parameter.
 * The default name of the hook method is <strong>dataOut</strong>
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataOutHook {

    /**
     * The defining class of the hook
     */
    Class<?> definingClass();

    /**
     * The method name of the hook
     */
    String method() default "dataOut";

    /**
     * The tag of the hook
     */
    String tag() default "";
}
