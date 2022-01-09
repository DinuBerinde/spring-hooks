package com.dinuberinde.webhooks;

import java.lang.annotation.*;


/**
 * This webhook will be invoked after the
 * target annotated method finished normally or with an exception.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostWebhook {

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

