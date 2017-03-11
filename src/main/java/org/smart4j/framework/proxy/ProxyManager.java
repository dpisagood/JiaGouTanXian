package org.smart4j.framework.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by DP on 2016/11/1.
 * 代理管理器
 * 提供一个创建代对象的方法，输入一个目标类和一组Proxy接口实现，输出一个代理对象，让它来创建所有的代理对象。
 */
public class ProxyManager {
    public static <T> T createProxy(final Class<?> targetClass, final List<Proxy> proxyList){
        return (T) Enhancer.create(targetClass,
                                    new MethodInterceptor() {
            @Override
            public Object intercept(Object targetObject,
                                    Method targetMethod,
                                    Object[] methodParams,
                                    MethodProxy methodProxy)
                    throws Throwable {
                //执行链式代理，真正结束目标方法调用的是在doProxyChain()执行的，
                // 其实那些不同切面类为目标类提供的通知全在切面类中的doProxyChain()中执行的
                    return new ProxyChain(targetClass,
                                        targetObject,
                                        targetMethod,
                                        methodProxy,
                                        methodParams,
                                            proxyList).doProxyChain();
            }
        });
    }
}
