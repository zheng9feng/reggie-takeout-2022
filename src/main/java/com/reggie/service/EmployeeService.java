package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.ResponseInfo;
import com.reggie.entity.Employee;

import javax.servlet.http.HttpServletRequest;

/**
 * @author m0v1
 * @date 2022年09月30日 22:27
 */
public interface EmployeeService extends IService<Employee> {

    /**
     * 登录逻辑处理
     * 1.将页面提交的密码进行 MD5 加密处理
     * 2.根据页面提交的用户名 username 查询数据库
     * 3.如果没有查询到则返回登录失败结果
     * 4.密码比对，如果不一致则返回登录结果
     * 5.查看员工状态，如果已为禁用状态，则返回员工已禁用结果
     * 6.登录成功，将员工 id 存入 session 并返回登录成功结果
     *
     * @param httpServletRequest 请求
     * @param employee           前端录入员工数据
     * @return
     */
    ResponseInfo<Employee> handleLogin(HttpServletRequest httpServletRequest, Employee employee);

    /**
     * 为新增员工预设初始密码
     *
     * @param employee
     */
    void setDefaultPassword(Employee employee);

    /**
     * 保存新增员工
     *
     * @param request
     * @param employee
     */
    void saveNewEmployee(HttpServletRequest request, Employee employee);

    /**
     * 分页查询
     *
     * @param page     当前页
     * @param pageSize 每页行数
     * @param name     员工名称(用于模糊查询)
     * @return
     */
    ResponseInfo<Page<Employee>> listByPage(int page, int pageSize, String name);
}
