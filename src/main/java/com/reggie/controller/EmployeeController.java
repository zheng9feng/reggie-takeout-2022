package com.reggie.controller;

import com.reggie.common.ResponseInfo;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author m0v1
 * @date 2022年09月30日 22:29
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param httpServletRequest
     * @param employee
     */
    @PostMapping("/login")
    public ResponseInfo<Employee> login(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {
        return employeeService.handleLogin(httpServletRequest, employee);
    }

    /**
     * 员工退出登录
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ResponseInfo<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return ResponseInfo.success("退出成功！");
    }

    @PostMapping
    public ResponseInfo<String> addEmployee(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {
        log.info("新增员工的信息：{}", employee.toString());

        // 为新增员工设置初始密码
        employeeService.setDefaultPassword(employee);

        // 保存新增员工数据
        employeeService.saveNewEmployee(httpServletRequest, employee);
        return ResponseInfo.success("添加员工成功");
    }

}
