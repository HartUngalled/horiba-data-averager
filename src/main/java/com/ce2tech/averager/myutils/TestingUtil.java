package com.ce2tech.averager.myutils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestingUtil {

    public static Object invokePrivateMethod(String methodName, Object classInstance) {
        try {
            Method method = classInstance.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(classInstance);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

}
