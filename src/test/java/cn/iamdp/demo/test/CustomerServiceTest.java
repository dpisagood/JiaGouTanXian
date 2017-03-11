package cn.iamdp.demo.test;

import cn.iamdp.demo.hepler.DatabaceHelper;
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
 * 数据库服务层测试类，通过就近读取test/resources/根目录中的/sql/config_init.properties文件
 * 即测试数据库配置文件，进行数据库测试
 */
public class CustomerServiceTest {
    private static final CustomerService  customerService;


    static{
        customerService=new CustomerService();
    }
    @Before
    public  void  init(){//测试之前初始化数据库将这个数据库文件进行执行后，测试数据库就会变回原来的样子
        //TODO 初始化数据库
        DatabaceHelper.executeSqlFile("sql/customer_init.sql");

    }

    @Test
    public void getCustomerListTest() throws Exception{
        List<Customer> customersList =customerService.getCustomerList();
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
