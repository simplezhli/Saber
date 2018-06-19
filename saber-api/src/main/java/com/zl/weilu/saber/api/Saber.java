package com.zl.weilu.saber.api;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Saber {

    static final Map<Class<?>, Constructor<? extends UnBinder>> BINDINGS = new LinkedHashMap<>();

    @NonNull
    @UiThread
    public static UnBinder bind(@NonNull Object target) {
        return createBinding(target);
    }

    private static UnBinder createBinding(@NonNull Object target) {
        Class<?> targetClass = target.getClass();
        Constructor<? extends UnBinder> constructor = findBindingConstructorForClass(targetClass);

        if (constructor == null) {
            return UnBinder.EMPTY;
        }

        //noinspection TryWithIdenticalCatches Resolves to API 19+ only type.
        try {
            return constructor.newInstance(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }
    }

    @Nullable
    @CheckResult
    @UiThread
    private static Constructor<? extends UnBinder> findBindingConstructorForClass(Class<?> cls) {
        Constructor<? extends UnBinder> bindingCtor = BINDINGS.get(cls);
        if (bindingCtor != null) {
            return bindingCtor;
        }
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            return null;
        }
        try {
            Class<?> bindingClass = cls.getClassLoader().loadClass(clsName + "_Providers");
            //noinspection unchecked
            bindingCtor = (Constructor<? extends UnBinder>) bindingClass.getConstructor(cls);
        } catch (ClassNotFoundException e) {
            bindingCtor = findBindingConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        BINDINGS.put(cls, bindingCtor);
        return bindingCtor;
    }


}