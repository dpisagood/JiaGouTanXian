package org.smart4j.framework.proxy;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 链式代理类
 * Created by DP on 2016/11/1.
 */
public class ProxyChain {

    private final Class<?> targetClass;//目标类
    private final Object targetObject;//目标对象
    private final Method  targetMethod;//目标方法
    private final MethodProxy methodProxy;//方法代理
    private final Object[] methodParams;//方法参数

    private List<Proxy>  proxyList=new ArrayList<Proxy>();//代理列表
    private int proxyIndex=0;//代理索引


    public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod, MethodProxy methodProxy,
                      Object[] methodParams, List<Proxy> proxyList){
        this.targetClass=targetClass;
        this.targetObject=targetObject;
        this.targetMethod=targetMethod;

        this.methodProxy=methodProxy;
        this.methodParams=methodParams;
        this.proxyList=proxyList;

    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    public Object doProxyChain() throws Throwable{//执行相应的代理
        Object methodResult;
        if (proxyIndex<proxyList.size()){//每次执行后索引加一
            //这是最大的猫腻，在为目标类生成代理时实现的intercept方法的时候，通过生成一个ProxyChain并调用这个方法，
            // 然后就可以将所有的切面类中的逻辑在目标方法执行之前全部执行
            methodResult=proxyList.get(proxyIndex++).doProxy(this);
        }else {
            methodResult=methodProxy.invokeSuper(targetObject,methodParams);//最后执行目标对象的业务逻辑
        }
        return methodResult;
    }
}
