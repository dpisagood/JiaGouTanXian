package org.smart4j.framework.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.annotation.Aspect;
import org.smart4j.framework.annotation.Transaction;
import org.smart4j.framework.proxy.AspectProxy;
import org.smart4j.framework.proxy.Proxy;
import org.smart4j.framework.proxy.ProxyManager;
import org.smart4j.framework.proxy.TransactionProxy;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 方法拦截助手类
 * 分析:这个类初始化整个简单的AOP框架，使得整个切面逻辑可以在上下文应用
 * 但是整个AOP框架的功能缺陷很大，和Spring-AOP有很大的区别
 * spring aop中可以通过Aspect指示器实现更细粒度的拦截，如只拦截一个类中的一个方法。
 * 而且可以将任何一个简单的bean通过@Aspect变成切面类，此aop必须是AspectProxy的实现类才能使用@Aspect变为切面类
 * 并可以将切面类中任何一个的方法（除去构造器）通过相应注解（如@Before）变成相应的通知，就是本书中所说的增强
 * 不用像这个简单的aop框架切面类必须继承抽象类AspectProxy，去实现事先定义好的通知具体看AspectProxy，
 * Created by DP on 2016/11/14.
 */
public final class AopHelper {
    private static final Logger LOGGER= LoggerFactory.getLogger(AopHelper.class);

    static {//初始化整个AOP框架
        try{
            //获得切面类及其目标类集合之间的映射关系
            Map<Class<?>,Set<Class<?>>> proxyMap=createProxyMap();
            //得到目标类和拦截他的切面类集合的映射
            Map<Class<?>,List<Proxy>> targetMap=createTargetMap(proxyMap);
            for(Map.Entry<Class<?>,List<Proxy>> targetEntry :targetMap.entrySet()){//为目标类创建代理对象
                Class<?> targetClass=targetEntry.getKey();
                List<Proxy> proxyList=targetEntry.getValue();
                Object proxy= ProxyManager.createProxy(targetClass,proxyList);
                BeanHelper.setBean(targetClass,proxy);//将目标类Class对象与其代理对象存入全局beanMap中供取用
            }
        }catch (Exception e){
            LOGGER.error("aop failure",e);
        }
    }

    /**
     * 通过获取Aspect注解中设置的注解类，若该注解类不是Aspect类，则可以调用ClassHeloper#getClassSetByAnnotation方法
     * 获取相关类，并把这些类存放目标类集合中，最终返回这个集合
     * @param aspect
     * @return
     * @throws Exception
     */
    private static Set<Class<?>> createTargetClassSet(Aspect aspect) throws Exception{
        Set<Class<?>> targetClassSet=new HashSet<Class<?>>();
        Class<? extends Annotation> annotation=aspect.value();
        if(annotation!=null && !annotation.equals(Aspect.class)){
            targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(annotation));
        }
        return targetClassSet;
    }

    /**
     * 得到类中切面类与他们所需要代理的目标类的映射集合。用于为他们创建代理对象
     * @return
     * @throws Exception
     */
    private static Map<Class<?>,Set<Class<?>>> createProxyMap() throws Exception{
        Map<Class<?>,Set<Class<?>>> proxyMap=new HashMap<Class<?>,Set<Class<?>>>();
        addAspectProxy(proxyMap);
        addTransactionProxy(proxyMap);
        return proxyMap;
    }

    /**
     * 这个方法用于获取切面类及其目标类集合之间的映射关系，一个切面类可以对应一个或者多个目标类
     * @param proxyMap
     * @throws Exception
     */
    private static void addAspectProxy(Map<Class<?>,Set<Class<?>>> proxyMap)throws Exception{
        //得到AspectProxy抽象类的所有子类和实现类Class对象集合
        Set<Class<?>> proxyClassSet=ClassHelper.getClassSetBySuper(AspectProxy.class);
        for(Class<?> proxyClass:proxyClassSet){
            //如果这个AspectProxy的实现类带有@Aspect注解
            if(proxyClass.isAnnotationPresent(Aspect.class)){
                Aspect aspect=proxyClass.getAnnotation(Aspect.class);//得到这个类中@Aspect注解对象
                //调用createTargetClassSet方法得到所有目标类对象
                Set<Class<?>> targetClassASet= null;
                targetClassASet = createTargetClassSet(aspect);//所有带有这个切面类注解的目标类集合
                proxyMap.put(proxyClass,targetClassASet);//添加切面类与目标类集合之间的映射关系
            }
        }
    }

    /**
     *  创建事务管理代理类和目标对象集合（也就是类中某个方法可能需要需要事务管理的类）
     *  我们在service类中也就是有@service的类中添加需要事务管理带有@Transaction的方法，当这些方法被执行时就会被
     *  代理类拦截，添加事务管理，详见AspectProxy类
     * @param proxyMap
     */
    private static void addTransactionProxy(Map<Class<?>,Set<Class<?>>> proxyMap){
        Set<Class<?>> serviceClassSet=ClassHelper.getServiceClassSet();
        proxyMap.put(TransactionProxy.class,serviceClassSet);

    }


    /**
     * 这个方法用于得到目标类与切面类的对象集合的映射关系，
     * 分析：一个目标类可以含有多个注解，就可能存在这几个注解都有相应的切面类对他们进行拦截。
     * 即这个目标类就有好几个切面类对其进行增强，所以就成了一个代理链
     * @param proxyMap
     * @return
     * @throws Exception
     */
    private static Map<Class<?>,List<Proxy>> createTargetMap(Map<Class<?>,Set<Class<?>>> proxyMap) throws Exception{
        Map<Class<?>,List<Proxy>> targetMap=new HashMap<Class<?>, List<Proxy>>();
        for(Map.Entry<Class<?>,Set<Class<?>>> proxyEntry:proxyMap.entrySet()){
            Class<?> proxyClass=proxyEntry.getKey();
            Set<Class<?>> targetClassSet=proxyEntry.getValue();
            for (Class<?> targetClass:targetClassSet){
                Proxy proxy=(Proxy) proxyClass.newInstance();
                if(targetMap.containsKey(targetClass)){
                    targetMap.get(targetClass).add(proxy);
                }else {
                    List<Proxy> proxyList=new ArrayList<Proxy>();
                    proxyList.add(proxy);
                    targetMap.put(targetClass,proxyList);
                }
            }
        }
        return targetMap;
    }
}
