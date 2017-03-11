package org.smart4j.framework.helper;

import org.smart4j.framework.annotation.Inject;
import org.smart4j.framework.utils.ArrayUtil;
import org.smart4j.framework.utils.CollectionUtil;
import org.smart4j.framework.utils.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 依赖注入助手类
 * 通过遍历所有bean类与bean实例之间的映射(BeanMap)，分别取出Bean类与Bean实例，进而通过反射获取类中所有的成员变量。
 * 继续遍历这些成员变量，在循环中判断当前成员变量是否带有Inject注解，若带有该注解，则从BeanMap中根据Bean类取出Bean实例
 * 最后用ReflectionUtil#setField方法来修改当前成员变量的值，也就是注入实例
 * Created by DP on 2016/10/27.
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
                            //Object beanFieldInstance=ReflectionUtil.newInstance(beanFieldClass);//这样做不知道是否可以，这样每次注入的都是新的实例化对象
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
