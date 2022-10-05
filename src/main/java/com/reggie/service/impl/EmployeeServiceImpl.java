package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.ResponseInfo;
import com.reggie.entity.Employee;
import com.reggie.mapper.EmployeeMapper;
import com.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author m0v1
 * @date 2022年09月30日 22:28
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    public static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public ResponseInfo<Employee> handleLogin(HttpServletRequest httpServletRequest, Employee employee) {
        String password = employee.getPassword();
        password = encodePassword(password);

        // 根据员工名查询数据库
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, employee.getUsername());
        Employee sameName = employeeMapper.selectOne(wrapper);

        if (sameName == null) {
            return ResponseInfo.error("该员工不存在，登录失败！");
        }
        if (!sameName.getPassword().equals(password)) {
            return ResponseInfo.error("密码有误，登录失败！");
        }
        if (sameName.getStatus() == 0) {
            return ResponseInfo.error("该账号已被禁用，登录失败！");
        }

        // 员工认证通过，保存数据到session
        updateSession(httpServletRequest, sameName);

        return ResponseInfo.success(sameName);
    }

    @Override
    public void setDefaultPassword(Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex(DEFAULT_PASSWORD.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void saveNewEmployee(HttpServletRequest request, Employee employee) {
        Long employeeID = ((Long) request.getSession().getAttribute("employee"));

        employee.setCreateUser(employeeID);
        employee.setUpdateUser(employeeID);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        employeeMapper.insert(employee);
    }

    @Override
    public ResponseInfo<Page> listByPage(int page, int pageSize, String name) {
        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序添加
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        this.page(pageInfo, queryWrapper);

        return ResponseInfo.success(pageInfo);
    }

    /**
     * 更新会话
     *
     * @param httpServletRequest
     * @param sameName
     */
    private void updateSession(HttpServletRequest httpServletRequest, Employee sameName) {
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("employee", sameName.getId());
    }

    /**
     * 对密码进行编码
     *
     * @param password
     * @return
     */
    private String encodePassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
    }
}
