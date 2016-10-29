package org.smart4j.framework.utils;


import org.slf4j.Logger;

/**
 * Created by DP on 2016/10/29.
 */
public class JsonUtil {
    private static final Logger LOGGER= org.slf4j.LoggerFactory.getLogger(CodecUtil.class);

    private static final ObjectMapper OBJECT_MAPPER=new ObjectMapper();

    /**
     * 将POJO转为JSON
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String toJson(T obj){
        String json;
        try{
            json=OBJECT_MAPPER.writeValueAsString(obj);
        }catch (Exception e){
            LOGGER.error("concert POJO to Json failure",e);
            throw new RuntimeException(e);
        }
        return json;
    }

    public static <T> T fromJson(String json,Class<T> type){
        T pojo;
        try{
            pojo=OBJECT_MAPPER.readValue(json,type);
        }catch (){
            LOGGER.error("concert  Json to POJO failure",e);
            throw new RuntimeException(e);
        }
        return pojo;
    }

}
