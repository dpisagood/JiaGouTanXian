package cn.iamdp.demo.test;

import cn.iamdp.demo.model.Customer;
import cn.iamdp.demo.service.CustomerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wl on 2016/9/25.
 */
public class CustomerServiceTest {
    private static final CustomerService  customerService;


    static{
        customerService=new CustomerService();
    }
    @Before
    public  void  init(){
        //TODO 初始化数据库
    }

    @Test
    public void getCustomerListTest() throws Exception{
        List<Customer> customersList =customerService.getCustomerList("");
        Assert.assertEquals(2,customersList.size());//测试里面这个数组里面是不是含有两个值
    }

    @Test
    public void getCustomerTest() throws Exception{
        long id=1;
        Customer customer=customerService.getCustomer(id);
        Assert.assertNotNull(customer);
    }

    @Test
    public void createCustomerTest() throws Exception{
        Map<String,Object> fieldMap=new HashMap<String,Object>();
        fieldMap.put("name","customer100");
        fieldMap.put("contact","John");
        fieldMap.put("telephone","123456789");
        boolean result=customerService.createCustomer(fieldMap);
        Assert.assertTrue(result);
    }


    @Test
    public void updateCustomerTest() throws Exception{
        long id=1;
        Map<String,Object> fieldMap=new HashMap<String, Object>();
        fieldMap.put("contact","Eric");
        boolean result=customerService.updateCustomer(id,fieldMap);
        Assert.assertTrue(result);
    }

    @Test
    public void deleteCustomerTest() throws Exception{
        long id=1;
        boolean result=customerService.deleteCustomer(id);
        Assert.assertTrue(result);
    }

}
