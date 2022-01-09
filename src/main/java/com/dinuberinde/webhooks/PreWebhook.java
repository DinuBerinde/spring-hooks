package com.dinuberinde.webhooks;

import java.lang.annotation.*;

/**
 * This webhook will be invoked before the
 * target annotated method will be invoked.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreWebhook {

    /**
     * The class type of the webhook
     */
    Class<?>[] type();

    /**
     * The method name of the webhook
     */
    String[] method() default {};

    /**
     * The tag of the webhook
     */
    String tag() default "";
}
