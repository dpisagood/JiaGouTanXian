package org.smart4j.framework.bean;

import org.smart4j.framework.utils.CastUtil;
import org.smart4j.framework.utils.CollectionUtil;

import java.util.Map;

/**
 * Created by DP on 2016/10/28.
 */

/**
 * 参数对象，里面含有参数名和参数的map
 */
public class Param {

    private Map<String,Object> paramMap;

    public Param(Map<String,Object> paramMap){
        this.paramMap=paramMap;
    }

    /**
     * 根据参数名获取long型参数值
     * @param name
     * @return
     */
    public long getLong(String name){
        return CastUtil.castLong(paramMap.get(name));
    }

    public Map<String,Object> getMap(){
        return paramMap;
    }

    public boolean isEmpty(){
        return CollectionUtil.isEmpty(paramMap);
    }
}
