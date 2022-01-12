package com.dinuberinde.hooks;

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
public class HooksAOP {

    @Autowired
    private ApplicationContext context;

    private final static Logger logger = LoggerFactory.getLogger(HooksAOP.class);
    private final Map<String, Optional<Object>> hookObjectsCache = new ConcurrentHashMap<>();
    private final static Map<Class<?>, String> annotations = new HashMap<>() {{
       put(PreHook.class, "pre"); put(PostHook.class, "post"); put(ExceptionHook.class, "exception");
       put(DataInHook.class, "dataIn"); put(DataOutHook.class, "dataOut");
    }};

    /**
     * Handler of {@link PreHook} annotation.
     *
     * @param preHook the annotation
     */
    @Before("@annotation(preHook)")
    public void preHook(PreHook preHook) {
        try {
            callHooks(preHook, preHook.type(), preHook.method(), preHook.tag());
        } catch (Exception e) {
            logger.error("[PRE hook error]", e);
        }
    }

    /**
     * Handler of the {@link PostHook} annotation.
     *
     * @param postHook the annotation
     */
    @After("@annotation(postHook)")
    public void postHook(PostHook postHook) {
        try {
            callHooks(postHook, postHook.type(), postHook.method(), postHook.tag());
        } catch (Exception e) {
            logger.error("[POST hook error]", e);
        }
    }

    /**
     * Handler of the {@link ExceptionHook} annotation.
     *
     * @param exceptionHook the annotation
     * @param exception the exception
     */
    @AfterThrowing(pointcut = "@annotation(exceptionHook)", throwing = "exception")
    public void exceptionHook(ExceptionHook exceptionHook, Exception exception) {
        try {
            callHook(exceptionHook, exceptionHook.type(), exceptionHook.method(), new Class[]{String.class, Exception.class}, new Object[]{exceptionHook.tag(), exception});
        } catch (Exception e) {
            logger.error("[EXCEPTION hook error]", e);
        }
    }

    /**
     * Handler of the {@link DataInHook} annotation.
     *
     * @param joinPoint the proceeding join point
     * @param dataInHook the annotation
     * @return the result of the target annotated method
     * @throws Throwable if errors occur
     */
    @Around("@annotation(dataInHook)")
    public Object dataInHook(ProceedingJoinPoint joinPoint, DataInHook dataInHook) throws Throwable {
        Object[] args = joinPoint.getArgs();

        try {
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
                        if (!parametersType[i].equals(dataInHook.dataType())) {
                            throw new IllegalArgumentException("Argument type mismatch. @DataInHook data type was " + dataInHook.dataType().getName() + " but "
                            + parametersType[i].getName() + " was provided for [" + method + "] of [" + method.getDeclaringClass().getName()+ "]");
                        }

                        // we bind the result of the hook to the argument annotated with Data
                        args[i] = callHook(dataInHook, dataInHook.type(), dataInHook.method(), new Class[]{String.class}, new Object[]{dataInHook.tag()});
                        dataAnnotationFound = true;
                    }
                }
            }

            if (!dataAnnotationFound) {
                throw new IllegalArgumentException("Method " + method.getName() + " of " + method.getDeclaringClass().getName() + " has no @Data annotation");
            }

        } catch (Exception e) {
            logger.error("[DATA-IN hook error]", e);
        }

        return joinPoint.proceed(args);
    }

    /**
     * Handler of the {@link DataOutHook} annotation.
     *
     * @param joinPoint the join point
     * @param dataOutHook the annotation
     * @param result the result of the target annotated method
     */
    @AfterReturning(value = "@annotation(dataOutHook)", returning = "result")
    public void dataOutHook(JoinPoint joinPoint, DataOutHook dataOutHook, Object result) {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

            if (!method.getReturnType().equals(dataOutHook.dataType())) {
                throw new IllegalArgumentException("Return type of method [" + method + "] of " + method.getDeclaringClass().getName() +
                        " should have the same return type " + dataOutHook.dataType().getName() + " as the hook method [" + dataOutHook.method() + "] of " + dataOutHook.type().getName()
                );
            }

            callHook(dataOutHook, dataOutHook.type(), dataOutHook.method(), new Class[] {String.class, dataOutHook.dataType()}, new Object[]{dataOutHook.tag(), result});
        } catch (Exception e) {
            logger.error("[DATA-OUT hook error]", e);
        }
    }

    private void callHooks(Annotation annotation, Class<?>[] types, String[] hookMethods, String tag) throws NoSuchMethodException {
        for (int i = 0; i < types.length; i++) {
            callHook(annotation, types[i], getSafeHookMethodName(hookMethods, i, annotation), new Class[]{String.class}, new Object[]{tag});
        }
    }

    /**
     * It calls the hook method.
     * @param annotation the hook annotation
     * @param type the type class of the hook
     * @param methodName the method name of the hook
     * @param typeParameters the type parameters of the hook method
     * @param args the arguments to be passed to the method
     * @return the result of the hook method or null if the hook method returns void
     */
    private Object callHook(Annotation annotation, Class<?> type, String methodName, Class<?>[] typeParameters, Object[] args) throws NoSuchMethodException {
        Method hookMethod = type.getMethod(methodName, typeParameters);
        String hookName = annotations.get(annotation.annotationType());
        logger.debug("[" + hookName.toUpperCase() + " hook] calling method [" + hookMethod + "] of [" + type.getName() + "]");
        Optional<Object> hookObject = hookObjectsCache.computeIfAbsent(getHookObjectKey(type, methodName), k -> getHookObject(type));
        return hookObject.map(instance -> ReflectionUtils.invokeMethod(hookMethod, instance, args)).orElse(null);
    }


    /**
     * It returns the hook object instance to be invoked.
     * First it looks for a Spring bean and if no bean is found then
     * a new instance of the class will be created.
     * The objects are recycled using a cache.
     * @param type the type class of the hook
     * @return the optional object instance of the hook
     */
    private Optional<Object> getHookObject(Class<?> type) {
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

    private static String getSafeHookMethodName(String[] methods, int index, Annotation annotation) {
        if (index < methods.length) {
            return methods[index];
        }
        // default hook name
        return annotations.get(annotation.annotationType());
    }

    private static String getHookObjectKey(Class<?> type, String method) {
        return type.getName() + "@" + method;
    }
}
