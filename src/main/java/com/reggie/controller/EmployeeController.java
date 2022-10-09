package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.ResponseInfo;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 新增员工
     *
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PostMapping
    public ResponseInfo<String> addEmployee(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {
        log.info("新增员工的信息：{}", employee.toString());

        // 为新增员工设置初始密码
        employeeService.setDefaultPassword(employee);

        // 保存新增员工数据
        employeeService.saveNewEmployee(httpServletRequest, employee);
        return ResponseInfo.success("添加员工成功");
    }

    /**
     * 根据员工ID查询员工信息
     *
     * @param id 员工ID
     * @return
     */
    @GetMapping("/{id}")
    public ResponseInfo<Employee> getEmployeeById(@PathVariable Long id) {
        log.info("根据员工ID查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return ResponseInfo.success(employee);
        }

        return ResponseInfo.error("没有查询到该员工的信息!");
    }

    /**
     * 分页查询员工信息
     *
     * @param page     当前页
     * @param pageSize 每页行数
     * @param name     员工名称
     * @return
     */
    @GetMapping("/page")
    public ResponseInfo<Page<Employee>> listEmployee(int page,
                                                     int pageSize,
                                                     String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
        return employeeService.listByPage(page, pageSize, name);
    }

    /**
     * 用户启用/禁用状态更新
     *
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public ResponseInfo<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        Long empID = (Long) request.getSession().getAttribute("employee");
        employeeService.updateById(employee);
        return ResponseInfo.success("员工修改信息成功");
    }
}
