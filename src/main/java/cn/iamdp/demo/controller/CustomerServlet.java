package cn.iamdp.demo.controller;

import cn.iamdp.demo.model.Customer;
import cn.iamdp.demo.service.CustomerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by wl on 2016/9/25.
 */
@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {
    private CustomerService customerService;

    @Override
    public void init() throws ServletException {
        customerService=new CustomerService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Customer> customersList=customerService.getCustomerList();
        request.setAttribute("customerList",customersList);
        request.getRequestDispatcher("WEB-INF/view/customer.jsp").forward(request,response);
    }
}
