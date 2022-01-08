package com.dinuberinde.webhooks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataOutWebhook {
    Class<?> type();
    String method() default "dataOut";
    Class<?> dataType();
    String tag() default "";
}