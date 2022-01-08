package com.dinuberinde.webhooks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreWebhook {
    Class<?>[] type();
    String[] method() default {};
    String tag() default "";
}
