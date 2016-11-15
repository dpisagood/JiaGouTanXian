package org.smart4j.framework.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.annotation.Aspect;
import org.smart4j.framework.annotation.Controller;
import org.smart4j.framework.proxy.AspectProxy;

import java.lang.reflect.Method;

/**
 * 拦截Controller所有的方法
 * 实现before和after方法进行日志记录
 * 加载器会在初始化的时候加载@Aspect注解，并为解析里面的value值，为这个value创建代理对象，并拦截里面的所有方法
 * spring aop中可以通过Aspect指示器实现更细粒度的拦截，如只拦截一个方法，而且可以将任何一个简单的bean通过@Aspect变成切面类，
 * 并可以将任何一个普通的方法通过相应注解变成相应的通知，就是本书中所说的增强
 * 不用像这个类一样通知被限制在抽象类AspectProxy中
 * Created by DP on 2016/11/14.
 */
@Aspect(Controller.class)
public class ControllerAspect extends AspectProxy {

    private static final Logger LOGGER= LoggerFactory.getLogger(ControllerAspect.class);
    private long begin;


    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        LOGGER.debug("-------   begin   -------");
        LOGGER.debug(String.format("class: %s",cls.getName()));
        LOGGER.debug(String.format("method: %s",method.getName()));
        begin =System.currentTimeMillis();
    }

    @Override
    public void after(Class<?> cls, Method method, Object[] params) throws Throwable {
        LOGGER.debug(String.format("time: %dms",System.currentTimeMillis()-begin));
        LOGGER.debug("---------   end  -----------");
    }

}
