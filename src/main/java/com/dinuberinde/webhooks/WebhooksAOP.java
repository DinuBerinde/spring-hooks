package com.dinuberinde.webhooks;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Configuration
public class WebhooksAOP {

    @Autowired
    private ApplicationContext context;

    private final static Logger logger = LoggerFactory.getLogger(WebhooksAOP.class);
    private final Map<String, Optional<Object>> webhookObjectsCache = new ConcurrentHashMap<>();
    private final static Map<Class<?>, String> annotations = new HashMap<>() {{
       put(PreWebhook.class, "pre"); put(PostWebhook.class, "post"); put(ExceptionWebhook.class, "exception");
       put(DataInWebhook.class, "dataIn"); put(DataOutWebhook.class, "dataOut");
    }};

    /**
     * Handler of {@link PreWebhook}. The webhook will be invoked before the
     * target annotated method will be invoked.
     *
     * @param preWebhook the annotation
     */
    @Before("@annotation(preWebhook)")
    public void preHook(PreWebhook preWebhook) {
        try {
            callWebhooks(preWebhook, preWebhook.type(), preWebhook.method(), preWebhook.tag());
        } catch (Exception e) {
            logger.error("[PRE Webhook error]", e);
        }
    }

    /**
     * Handler of {@link PostWebhook}. The webhook will be invoked after the
     * target annotated method finished normally or with an exception.
     *
     * @param postWebhook the annotation
     */
    @After("@annotation(postWebhook)")
    public void postHook(PostWebhook postWebhook) {
        try {
            callWebhooks(postWebhook, postWebhook.type(), postWebhook.method(), postWebhook.tag());
        } catch (Exception e) {
            logger.error("[POST Webhook error]", e);
        }
    }

    /**
     * Handler of the {@link ExceptionWebhook}. The webhook method will be invoked after
     * the target annotated method
     * with the exception thrown by the target annotated method.
     *
     * @param exceptionWebhook the annotation
     * @param exception the exception
     */
    @AfterThrowing(pointcut = "@annotation(exceptionWebhook)", throwing = "exception")
    public void exceptionHook(ExceptionWebhook exceptionWebhook, Exception exception) {
        try {
            callWebhook(exceptionWebhook, exceptionWebhook.type(), exceptionWebhook.method(), new Class[]{String.class, Exception.class}, new Object[]{exceptionWebhook.tag(), exception});
        } catch (Exception e) {
            logger.error("[EXCEPTION Webhook error]", e);
        }
    }

    /**
     * Handler of the {@link DataInWebhook}. The target annotated method
     * will be invoked with the result returned by the webhook method.
     *
     * @param joinPoint the proceeding join point
     * @param dataInWebhook the annotation
     * @return the result of the target annotated method
     * @throws Throwable if errors occur
     */
    @Around("@annotation(dataInWebhook)")
    public Object dataInHook(ProceedingJoinPoint joinPoint, DataInWebhook dataInWebhook) throws Throwable {
        try {
            Object[] args = joinPoint.getArgs();
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Class<?>[] parametersType = method.getParameterTypes();

            boolean dataAnnotationFound = false;
            for (int i = 0; i < args.length; i++) {
                for (Annotation paramAnnotation : parameterAnnotations[i]) {

                    if (dataAnnotationFound) {
                        throw new IllegalArgumentException("Method [" + method + "] of [" + method.getDeclaringClass().getName() + "] can contain at most one @Data annotation");
                    }

                    if (paramAnnotation instanceof Data) {
                        if (!parametersType[i].equals(dataInWebhook.dataType())) {
                            throw new IllegalArgumentException("Argument type mismatch. @DataInWebhook data type was " + dataInWebhook.dataType().getName() + " but "
                            + parametersType[i].getName() + " was provided for [" + method + "] of [" + method.getDeclaringClass().getName()+ "]");
                        }

                        // we bind the result of the webhook to the argument annotated with Data
                        args[i] = callWebhook(dataInWebhook, dataInWebhook.type(), dataInWebhook.method(), new Class[]{String.class}, new Object[]{dataInWebhook.tag()});
                        dataAnnotationFound = true;
                    }
                }
            }

            if (!dataAnnotationFound) {
                throw new IllegalArgumentException("Method " + method.getName() + " of " + method.getDeclaringClass().getName() + " has no @Data annotation");
            }

            return joinPoint.proceed(args);
        } catch (Exception e) {
            logger.error("[DATA-IN webhook error]", e);
            return null;
        }
    }

    /**
     * Handler of the {@link DataOutWebhook}. The result of the target annotated method
     * will be provided as argument to the webhook method.
     *
     * @param joinPoint the join point
     * @param dataOutWebhook the annotation
     * @param result the result of the target annotated method
     */
    @AfterReturning(value = "@annotation(dataOutWebhook)", returning = "result")
    public void dataOutHook(JoinPoint joinPoint, DataOutWebhook dataOutWebhook, Object result) {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

            if (!method.getReturnType().equals(dataOutWebhook.dataType())) {
                throw new IllegalArgumentException("Return type of method [" + method + "] of " + method.getDeclaringClass().getName() +
                        " should have the same return type " + dataOutWebhook.dataType().getName() + " as the webhook method [" + dataOutWebhook.method() + "] of " + dataOutWebhook.type().getName()
                );
            }

            callWebhook(dataOutWebhook, dataOutWebhook.type(), dataOutWebhook.method(), new Class[] {String.class, dataOutWebhook.dataType()}, new Object[]{dataOutWebhook.tag(), result});
        } catch (Exception e) {
            logger.error("[DATA-OUT webhook error]", e);
        }
    }

    private void callWebhooks(Annotation annotation, Class<?>[] types, String[] webhookMethods, String tag) throws NoSuchMethodException {
        for (int i = 0; i < types.length; i++) {
            callWebhook(annotation, types[i], getSafeWebhookMethodName(webhookMethods, i, annotation), new Class[]{String.class}, new Object[]{tag});
        }
    }

    /**
     * It calls the webhook method.
     * @param annotation the webhook annotation
     * @param type the type class of the webhook
     * @param methodName the method name of the webhook
     * @param typeParameters the type parameters of the webhook method
     * @param args the arguments to be passed to the method
     * @return the result of the webhook method or null if the webhook method returns void
     */
    private Object callWebhook(Annotation annotation, Class<?> type, String methodName, Class<?>[] typeParameters, Object[] args) throws NoSuchMethodException {
        Method webhookMethod = type.getMethod(methodName, typeParameters);
        String webhookName = annotations.get(annotation.annotationType());
        logger.debug("[" + webhookName.toUpperCase() + " Webhook] calling method [" + webhookMethod + "] of [" + type.getName() + "]");
        Optional<Object> webhookObject = webhookObjectsCache.computeIfAbsent(getWebhookObjectKey(type, methodName), k -> getWebhookObject(type));
        return webhookObject.map(instance -> ReflectionUtils.invokeMethod(webhookMethod, instance, args)).orElse(null);
    }


    /**
     * It returns the webhook object instance to be invoked.
     * First it looks for a Spring bean and if no bean is found then
     * a new instance of the class will be created.
     * The objects are recycled using a cache.
     * @param type the type class of the webhook
     * @return the optional object instance of the webhook
     */
    private Optional<Object> getWebhookObject(Class<?> type) {
        Optional<Object> springBean = getSpringBean(type);
        if (springBean.isPresent()) {
            return springBean;
        } else {
            return newConstructorInstance(type);
        }
    }

    /**
     * Returns a Spring bean from the application context.
     * @param type the type of the bean to return
     * @return the Spring bean or null if the bean does not exist
     */
    private Optional<Object> getSpringBean(Class<?> type) {
        try {
            return Optional.of(context.getBean(type));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * It creates a new object instance of a type.
     * @param type the type
     * @return an optional Object created
     */
    private Optional<Object> newConstructorInstance(Class<?> type) {
        try {
            return Optional.of(type.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            logger.error("An error occurred while creating an instance of " + type.getName(), e);
            return Optional.empty();
        }
    }

    private static String getSafeWebhookMethodName(String[] methods, int index, Annotation annotation) {
        if (index < methods.length) {
            return methods[index];
        }
        // default webhook name
        return annotations.get(annotation.annotationType());
    }

    private static String getWebhookObjectKey(Class<?> type, String method) {
        return type.getName() + "@" + method;
    }
}
