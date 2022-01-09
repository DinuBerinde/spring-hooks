package com.dinuberinde.webhooks;

import java.lang.annotation.*;


/**
 * The result of the target annotated method will be provided as argument to the webhook method.
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
