package org.smart4j.framework.bean;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.net.PortUnreachableException;

/**
 * Created by DP on 2016/10/28.
 */
public class Data {

    private Object model;

    public Data(Object model){
        this.model=model;
    }

    public Object getModel(){
        return model;
    }
}

