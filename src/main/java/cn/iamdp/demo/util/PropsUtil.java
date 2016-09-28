package cn.iamdp.demo.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 属性文件工具类,读取配置文件中的各数据类型的数据
 * Created by wl on 2016/9/27.
 */
public final class PropsUtil {
    private static final Logger LOGGER= LoggerFactory.getLogger(PropsUtil.class);

    /**
     * 加载属性文件
     * @param fileName
     * @return
     */
    public static Properties loadProps(String fileName){
        Properties props= null;
        InputStream is=null;
        try{
                //当前线程利用类加载器加载配置文件，获得一个输入流
                //http://www.cnblogs.com/yjl49/archive/2012/08/08/2628502.html
                is=Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (is==null){
                     throw new FileNotFoundException(fileName+"fileName");
                }
                props=new Properties();
                props.load(is);
            } catch (IOException e) {
                LOGGER.error("load properties file failure",e);//使用日志来输出错误
            }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("load properties file failure",e);
                }
            }
        }
        return  props;
    }

    /**
     * 获取字符类型属性（默认值为空字符串）
     * @param props
     * @param key
     * @return 返回的是配置文件中的值，如果没有找到就返回默认的空字符，不会有空指针异常
     */
    public static String getString(Properties props,String key){
        return getString(props,key,"");
    }

    /**
     *获取字符型属性（可指定默认值）
     * @param props
     * @param key
     * @param defaultValue
     * @return 返回配置文件中相应的value，如果没有就用我们自己指定defaultValue
     */
    public static String getString(Properties props, String key, String defaultValue) {
        String value=defaultValue;
        if(props.containsKey(key)){
            value=props.getProperty(key);
        }
        return value;
    }

    /**
     * 获取数值型属性（默认值为0）
     * @param props
     * @param key
     * @return
     */
    public static int getInt(Properties props,String key){
        return getInt(props,key,0);
    }

    /**
     * 获取数值型属性（可指定默认值）
     * @param props
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getInt(Properties props, String key, int defaultValue) {
        int value=defaultValue;
        if(props.containsKey(key)){
            value=CastUtil.castInt(props.getProperty(key));
        }
        return value;
    }

    /**
     * 获取布尔型属性（默认值为false）
     * @param props
     * @param key
     * @return
     */
    public static boolean getBoolean(Properties props,String key){
        return getBoolean(props,key,false);
    }

    /**
     * 获取布尔型属性（可指定默认值）
     * @param props
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(Properties props, String key, boolean defaultValue) {
        boolean value=defaultValue;
        if(props.containsKey(key)){
            value=CastUtil.castBoolean(props.getProperty(key));
        }
        return value;
    }
}
