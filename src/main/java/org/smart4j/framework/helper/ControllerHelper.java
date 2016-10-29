package org.smart4j.framework.helper;

import org.apache.commons.collections4.CollectionUtils;
import org.smart4j.framework.annotation.Action;
import org.smart4j.framework.bean.Handler;
import org.smart4j.framework.bean.Request;
import org.smart4j.framework.utils.ArrayUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by DP on 2016/10/27.
 */

/**
 * 初始化Request和Handler之间的对应关系，请求中有请求url和请求方法，handler中有对应处理这个请求的控制器和对应的action方法
 * 在转发器中的得到用户的请求url和请求方法method后可以实例化一个Requestl类，然后通过这个对应关系找到相应handler然后进行处理
 */
public class ControllerHelper {
    //用于存放请求与处理器的映射关系（简称 Action Map）
    private static final Map<Request,Handler> ACTION_MAP=new HashMap<Request, Handler>();
    static {
        //获取所有的Controller类的Class对象
        Set<Class<?>> controllerClassSet=ClassHelper.getControllerClassSet();
        if(CollectionUtils.isEmpty(controllerClassSet)){
            //遍历这些Controller类的Class对象
            for(Class<?> controllerClass:controllerClassSet){
                //获取Controller类中定义的所有方法
                Method[] methods=controllerClass.getDeclaredMethods();
                if(ArrayUtil.isNotEmpty(methods)){
                    for(Method method :methods){
                        if(method.isAnnotationPresent(Action.class)){
                            //判断当前方法里是否含有Action注解
                            Action action=method.getAnnotation(Action.class);
                            //得到URL映射规则
                            String mapping=action.value();
                            if(mapping.matches("\\w+:/\\w")){//POST:/method1
                                String[] array=mapping.split(":");//分割成两个部分
                                if(ArrayUtil.isNotEmpty(array)&&array.length==2){
                                    String requestMethod=array[0];
                                    String requestPath=array[1];
                                    Request request=new Request(requestMethod,requestPath);
                                    Handler handler=new Handler(controllerClass,method);
                                    //初始化Action Map
                                    ACTION_MAP.put(request,handler);
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    /**
     * 获取Handler
     * @param requestMethod
     * @param requestPath
     * @return
     */
    public static Handler getHandler(String requestMethod,String requestPath){
        Request request=new Request(requestMethod,requestPath);
        return ACTION_MAP.get(request);
    }
}
