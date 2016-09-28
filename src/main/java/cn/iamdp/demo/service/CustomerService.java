package cn.iamdp.demo.service;

import cn.iamdp.demo.model.Customer;

import java.util.List;
import java.util.Map;

/**
 * Created by wl on 2016/9/25.
 */


public class CustomerService {

    /**
     * 获取用户列表
     * @param keyword
     * @return
     */
    public List<Customer> getCustomerList(String keyword){
        return null;
    }

    /**
     * 获取客户
     * @param id
     * @return
     */

    public Customer getCustomer(long id){
        return null;
    }

    /**
     * 创建客户
     * @param fieldMap
     * @return
     */
    public boolean createCustomer(Map<String,Object> fieldMap){
        return true;
    }

    /**
     * 更新客户
     * @param id
     * @param fieldMap
     * @return
     */
    public boolean updateCustomer(long id ,Map<String,Object> fieldMap){
        return false;
    }

    /**
     * 删除客户
     * @param id
     * @return
     */
    public boolean deleteCustomer(long id){
        return false;
    }
}
