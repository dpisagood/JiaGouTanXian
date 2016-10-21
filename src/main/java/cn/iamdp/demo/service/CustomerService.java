package cn.iamdp.demo.service;

import cn.iamdp.demo.hepler.DatabaceHelper;
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
        String sql="SELECT * FROM customer";
        return DatabaceHelper.queryEntityList(Customer.class,sql);
    }

    /**
     * 获取客户
     * @param id
     * @return
     */

    public Customer getCustomer(long id){
        String sql="SELECT * FROM customer WHERE id=?";
        return DatabaceHelper.queryEntity(Customer.class,sql,id);
    }

    /**
     * 创建客户
     * @param fieldMap
     * @return
     */
    public boolean createCustomer(Map<String,Object> fieldMap){
        return DatabaceHelper.insertEntity(Customer.class,fieldMap);
    }

    /**
     * 更新客户
     * @param id
     * @param fieldMap
     * @return
     */
    public boolean updateCustomer(long id ,Map<String,Object> fieldMap){
        return DatabaceHelper.updateEntity(Customer.class,id,fieldMap);
    }

    /**
     * 删除客户
     * @param id
     * @return
     */
    public boolean deleteCustomer(long id){
        return DatabaceHelper.deleteEntity(Customer.class,id);
    }
}
