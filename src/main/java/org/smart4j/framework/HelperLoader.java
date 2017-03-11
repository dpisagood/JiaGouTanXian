package org.smart4j.framework;

import org.smart4j.framework.annotation.Controller;
import org.smart4j.framework.helper.*;
import org.smart4j.framework.utils.ClassUtil;

/**
 * 初始化框架
 * 加载相应的Helper类
 * Created by DP on 2016/10/28.
 */
public class HelperLoader {
    public static void init(){
        Class<?> [] classList={
                ClassHelper.class,
                BeanHelper.class,
                AopHelper.class,//应比Ioc容器先加载
                IocHelper.class,
                ControllerHelper.class
        };
        for (Class<?> cls:classList){
            ClassUtil.loadClass(cls.getName(),true);
        }
    }

}
