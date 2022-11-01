package com.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.reggie.common.BaseContext;
import com.reggie.common.ResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录状态过滤器：在未登录情况下只允许访问登录页面，访问其他页面是重定向到登录页面
 *
 * @author m0v1
 * @date 2022年09月30日 23:41
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    /**
     * 路径匹配器，支持通配符
     */
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //强转
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", request.getRequestURI());

        //定义不需要处理的请求
        // TODO: 2022/10/2 url列表配置
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

        // 2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3.如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求：{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 4-1 判断后台用户登录状态，如果已登录，则直接放行
        Long employeeID = ((Long) request.getSession().getAttribute("employee"));
        if (employeeID != null) {
            log.info("用户已登录，用户id为：{}", employeeID);
            BaseContext.setCurrentId(employeeID);

            filterChain.doFilter(request, response);
            return;
        }

        // 4-2 判断前台用户登录状态
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }

        //5.如果未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(ResponseInfo.error("NOTLOGIN")));
    }

    /**
     * 检查请求URI和预设到url列表是否匹配
     *
     * @param urls       预设的请求列表
     * @param requestURI 请求
     * @return true-匹配 false-不匹配
     */
    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                // 匹配
                return true;
            }
        }
        // 不匹配
        return false;
    }
}
