package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.ResponseInfo;
import com.reggie.entity.Employee;
import com.reggie.mapper.EmployeeMapper;
import com.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

/**
 * @author m0v1
 * @date 2022年09月30日 22:28
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

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

    /**
     * 更新会话
     *
     * @param httpServletRequest
     * @param sameName
     */
    private void updateSession(HttpServletRequest httpServletRequest, Employee sameName) {
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("employee", sameName);
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
