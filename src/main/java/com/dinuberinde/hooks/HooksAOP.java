package com.dinuberinde.hooks;

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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Aspect
@Configuration
public class HooksAOP {

    @Autowired
    private ApplicationContext context;

    private static final Logger logger = LoggerFactory.getLogger(HooksAOP.class);
    private static final Map<Class<?>, String> annotations = new HashMap<>();

    /**
     * The cache map of the hook classes. Objects get created and recycled.
     */
    private final Map<String, Optional<Object>> hookObjectsCache = new ConcurrentHashMap<>();

    static {
        annotations.put(PreHook.class, "pre");
        annotations.put(PostHook.class, "post");
        annotations.put(ExceptionHook.class, "exception");
        annotations.put(DataOutHook.class, "dataIn");
        annotations.put(DataInHook.class, "dataOut");
    }

    /**
     * Handler of {@link PreHook} annotation.
     *
     * @param preHook the annotation
     */
    @Before("@annotation(preHook)")
    public void preHook(PreHook preHook) {
        try {
            callHooks(preHook, preHook.definingClass(), preHook.method(), preHook.tag());
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
            callHooks(postHook, postHook.definingClass(), postHook.method(), postHook.tag());
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
            callHook(exceptionHook, exceptionHook.definingClass(), exceptionHook.method(), exceptionHook.tag(), exception, null);
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

            boolean dataAnnotationFound = false;
            for (int i = 0; i < args.length; i++) {
                for (Annotation paramAnnotation : parameterAnnotations[i]) {

                    if (paramAnnotation instanceof DataIn) {

                        if (dataAnnotationFound) {
                            throw new IllegalArgumentException("Method [" + method + "]  can contain at most one @DataIn annotation");
                        }

                        Method hookMethod = findHookMethod(dataInHook.definingClass(), dataInHook.method());
                        if (!hookMethod.getReturnType().equals(args[i].getClass())) {
                            throw new IllegalArgumentException("Return type of method [" + dataInHook.method() + "] of [" + dataInHook.definingClass().getName() + "]" +
                                    " must have type " + args[i].getClass().getName() +  ", parameter annotated with @DataIn of method [" + method + "]");
                        }

                        // we supply the result of the hook method to the argument annotated with Data
                        args[i] = callHook(dataInHook, dataInHook.definingClass(), dataInHook.method(), dataInHook.tag());
                        dataAnnotationFound = true;
                    }
                }
            }

            if (!dataAnnotationFound) {
                throw new IllegalArgumentException("Method " + method.getName() + " of " + method.getDeclaringClass().getName() + " has no @Data annotated parameter");
            }

        } catch (Exception e) {
            logger.error("[DATA-IN hook error]", e);
        }

        return joinPoint.proceed(args);
    }

    /**
     * Handler of the {@link DataOutHook} annotation.
     *
     * @param dataOutHook the annotation
     * @param result the result of the target annotated method
     */
    @AfterReturning(value = "@annotation(dataOutHook)", returning = "result")
    public void dataOutHook(DataOutHook dataOutHook, Object result) {
        try {
            callHook(dataOutHook, dataOutHook.definingClass(), dataOutHook.method(), dataOutHook.tag(), null, result);
        } catch (Exception e) {
            logger.error("[DATA-OUT hook error]", e);
        }
    }

    private void callHooks(Annotation annotation, Class<?>[] definingClasses, String[] hookMethods, String tag) throws NoSuchMethodException {
        for (int i = 0; i < definingClasses.length; i++) {
            callHook(annotation, definingClasses[i], getSafeHookMethodName(hookMethods, i, annotation), tag);
        }
    }

    /**
     * It calls the hook method.
     * @param annotation the hook annotation
     * @param definingClass the defining class of the hook
     * @param methodName the method name of the hook
     * @param tag the tag of the hook if present
     * @param exception the exception of the hook if any
     * @param dataOut the data out of the hook if any
     * @return the result of the hook method or null if the hook method returns void
     */
    private Object callHook(Annotation annotation, Class<?> definingClass, String methodName, String tag, Exception exception, Object dataOut) throws NoSuchMethodException {
        Method hookMethod = findHookMethod(definingClass, methodName);
        Object[] args = hookMethod.getParameterTypes().length == 0 ? new Object[]{} : new Object[]{new Hook(tag, dataOut, exception)};
        String hookName = annotations.get(annotation.annotationType());
        logger.debug("[" + hookName.toUpperCase() + " hook] calling method [" + hookMethod + "] of [" + definingClass.getName() + "]");
        Optional<Object> hookObject = hookObjectsCache.computeIfAbsent(getHookObjectKey(definingClass, methodName), k -> getHookObject(definingClass));
        return hookObject.map(instance -> ReflectionUtils.invokeMethod(hookMethod, instance, args)).orElse(null);
    }

    private Object callHook(Annotation annotation, Class<?> definingClass, String methodName, String tag) throws NoSuchMethodException {
        return callHook(annotation, definingClass, methodName, tag, null, null);
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

    /**
     * It looks for the hook method. First it looks for a method definition with the {@link Hook} parameter
     * and if such a method does not exist then it looks for parameterless method definition.
     * @param definingClass the defining class of the hook
     * @param methodName the name of the hook method
     * @return the hook method
     * @throws NoSuchMethodException if no method was found for the given methodName
     */
    private static Method findHookMethod(Class<?> definingClass, String methodName) throws NoSuchMethodException {
        List<Method> methods = Arrays.stream(definingClass.getMethods()).filter(m -> m.getName().equals(methodName)).collect(Collectors.toList());
        if (methods.isEmpty()) {
            throw new NoSuchMethodException("No method [" + methodName + "] definition found on [" + definingClass.getName() + "]");
        }

        // find method with Hook param definition
        Method method = findMethodWithParams(methods, new Class[]{Hook.class});
        if (method != null) {
            return method;
        }

        // find method with no param definition
        method = findMethodWithParams(methods, new Class[]{});
        if (method != null) {
            return method;
        }

        throw new NoSuchMethodException("No suitable method definition was found for [" + methodName + "] of [" + definingClass.getName() + "]");
    }

    private static Method findMethodWithParams(List<Method> methods, Class<?>[] paramTypes) {
        return methods.stream()
                .filter(method -> Arrays.equals(method.getParameterTypes(), paramTypes))
                .findAny()
                .orElse(null);
    }
}
