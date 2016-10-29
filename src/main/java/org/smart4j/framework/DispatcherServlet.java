package org.smart4j.framework;



import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.apache.commons.lang3.StringUtils;
import org.smart4j.framework.bean.Data;
import org.smart4j.framework.bean.Handler;
import org.smart4j.framework.bean.Param;
import org.smart4j.framework.bean.View;
import org.smart4j.framework.helper.BeanHelper;
import org.smart4j.framework.helper.ConfigHelper;
import org.smart4j.framework.helper.ControllerHelper;
import org.smart4j.framework.utils.*;
import sun.reflect.generics.scope.MethodScope;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DP on 2016/10/28.
 */

/**
 * load-on-startup标记容器是否在启动的时候实例化并调用其init()方法的优先级。
 它的值表示servlet应该被载入的顺序
 当值为0或者大于0时，表示容器在应用启动时就加载并初始化这个servlet；
 如果值小于0或未指定时，则表示只有在第一次请求的容器才在该servlet调用初始化函数
 正值越小，servlet的优先级越高，应用启动时就越先加载。
 */
@WebServlet(urlPatterns = "/*",loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig servletConfig){
        //初始化相关的类
        HelperLoader.init();
        //获取Servlet对象（用于注册Servlet）
        ServletContext servletContext=servletConfig.getServletContext();
        //注册处理JSP的Servlet
        ServletRegistration jspServlet=servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath()+"*");
        //注册处理静态资源的默认Servlet
        ServletRegistration defaultServlet=servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath()+"*");
    }


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestMethod =request.getMethod().toLowerCase();
        //得到请求URL相对于Servlet配置的访问url的路径，比如本Servlet配置的访问url为“/*”则getPathInfo()得到的就是*和*后面的路径，但不包括参数
        String requestPath=request.getPathInfo();
        //获取Action处理器
        Handler handler= ControllerHelper.getHandler(requestMethod,requestPath);
        if(handler!=null){
            //获取Controller类及其Bean实例
            Class<?> controllerClass =handler.getControllerClass();
            Object controllerBean= BeanHelper.getBean(controllerClass);
            //创建请求参数对象
            Map<String,Object> paramMap=new HashMap<String ,Object>();
            Enumeration<String> paramNames=request.getParameterNames();
            while (paramNames.hasMoreElements()){
                String paramName=paramNames.nextElement();
                String paramValue=request.getParameter(paramName);
                paramMap.put(paramName,paramValue);
            }
            //将请求中包含的参数通过字符输入流的方式读取
            String body= CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
            if(StringUtil.isNotEmpty(body)){
                String[] params= StringUtils.split(body,"&");
                if(ArrayUtil.isNotEmpty(params)) {
                    for (String param:params){
                        String [] array=StringUtils.split(param,"=");
                        if (ArrayUtil.isNotEmpty(array)&&array.length==2){
                            String paramName=array[0];
                            String paramValue=array[1];
                            paramMap.put(paramName,paramValue);
                        }
                    }
                }
            }
            Param param=new Param(paramMap);
            //调用Action方法
            Method actionMethod =handler.getActionMethod();
            Object result= ReflectionUtil.invokeMethod(controllerBean,actionMethod,param);
            //处理Action方法的返回值
            if(result instanceof View){
                //返回JSP页面
                View view=(View) result;
                String path =view.getPath();
                if(StringUtil.isNotEmpty(path)){
                    if(path.startsWith("/")){
                        response.sendRedirect(request.getContextPath()+path);
                    }else{
                        Map<String,Object> model=view.getModel();
                        for (Map.Entry<String,Object> entry:model.entrySet()){
                            request.setAttribute(entry.getKey(),entry.getValue());
                        }
                        request.getRequestDispatcher(ConfigHelper.getAppJspPath()+path).forward(request,response);
                    }
                }

            }else if (result instanceof Data){
                Data data=(Data) result;
                Object model=data.getModel();
                if(model!=null){
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter writer=response.getWriter();//
                    String json=JsonUtil.toJson(model);
                    writer.write(json);
                    writer.flush();
                    writer.close();
                }
            }
        }

    }
}
