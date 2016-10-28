package org.smart4j.framework.helper;

import org.smart4j.framework.annotation.Inject;
import org.smart4j.framework.utils.ArrayUtil;
import org.smart4j.framework.utils.CollectionUtil;
import org.smart4j.framework.utils.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by DP on 2016/10/27.
 */

/**
 * 依赖注入助手类
 */
public final class IocHelper {
    static {
        //获取所有的Bean类与Bean实例之间的映射
        Map<Class<?>,Object> beanMap=BeanHelper.getBeanMap();
        if(CollectionUtil.isNotEmpty(beanMap)){
            //Map.Entry是Map声明的一个内部接口，此接口为泛型，定义为Entry<K,V>。它表示Map中的一个实体（一个key-value对）。接口中有getKey(),getValue方法。
            for (Map.Entry<Class<?>,Object> beanEntry:beanMap.entrySet()){
                //从BeanMap中获取Bean类Bean类实例
                Class<?> beanClass=beanEntry.getKey();
                Object beanInstance=beanEntry.getValue();
                //获取Bean类定义的所有的成员变量（简称 Bean Field）
                Field[] beanFields=beanClass.getDeclaredFields();
                if(ArrayUtil.isNotEmpty(beanFields)){
                    //遍历这个类中的所有成员变量
                    for (Field beanfield:beanFields){
                        if(beanfield.isAnnotationPresent(Inject.class)){//如果这个变量带有Inject注解的话
                            Class<?> beanFieldClass= beanfield.getType();
                            //通过它的Class对象在容器找到它的实例化对象
                            Object beanFieldInstance=beanMap.get(beanFieldClass);
                            if(beanFieldInstance!=null){
                                //然后用它的实例化对象去初始化这个拥有这个注解类的类
                                ReflectionUtil.setField(beanInstance,beanfield,beanFieldInstance);
                            }
                        }
                    }
                }

            }

        }
    }
}
