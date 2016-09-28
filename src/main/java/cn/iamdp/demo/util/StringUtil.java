package cn.iamdp.demo.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 * Created by wl on 2016/9/28.
 */
public final class StringUtil {


    /**
     * 判断字符串是否为空，空返回true,非空返回false
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        if(str!=null){
            str=str.trim();
        }
        return StringUtils.isEmpty(str);
    }

    /**
     * 判断字符串是否为非空，非空返回true,空返回true
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str){
        return  !isEmpty(str);
    }

}
