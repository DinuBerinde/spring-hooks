package com.dinuberinde.webhooks;

import java.lang.annotation.*;

/**
 * The target annotated method will be invoked with the result returned by the webhook method.
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
