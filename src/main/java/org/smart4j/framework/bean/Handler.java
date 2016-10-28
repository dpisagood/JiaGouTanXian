package org.smart4j.framework.bean;

import java.lang.reflect.Method;

/**
 * Created by DP on 2016/10/27.
 */
public class Handler {

    private Class<?> controllerClass;

    //Action方法
    private Method actionMethod;

    public Handler(Class<?> controllerClass,Method actionMethod){
        this.controllerClass=controllerClass;
        this.actionMethod=actionMethod;
    }

    public Class<?> getControllerClass(){
        return controllerClass;
    }

    public Method getActionMethod(){
        return actionMethod;
    }
}
