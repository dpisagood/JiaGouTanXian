package org.smart4j.framework.utils;

import javafx.beans.binding.StringBinding;
import org.slf4j.Logger;

import java.io.*;

/**
 * 流操作工具类
 * Created by DP on 2016/10/29.
 */
public final class StreamUtil {
    private static final Logger LOGGER= org.slf4j.LoggerFactory.getLogger(StreamUtil.class);

    /**
     * 从输出流中获取字符串
     * @param is
     * @return
     */
    public static String getString(InputStream is){
        StringBuilder sb=new StringBuilder();//非线程安全
        try {
            BufferedReader reader=new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line=reader.readLine())!=null){
                sb.append(line);
            }
        } catch (Exception e) {
            LOGGER.error("get String failure",e);
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
