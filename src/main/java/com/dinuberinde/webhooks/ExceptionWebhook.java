package com.dinuberinde.webhooks;

import java.lang.annotation.*;

/**
 * This webhook will be invoked with the exception thrown by the target annotated method,
 * when an error occurs.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionWebhook {

    /**
     * The class type of the webhook
     */
    Class<?> type();

    /**
     * The method name of the webhook
     */
    String method() default "exception";

    /**
     * The tag of the webhook
     */
    String tag() default "";
}
