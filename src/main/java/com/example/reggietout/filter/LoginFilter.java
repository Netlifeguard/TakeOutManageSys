package com.example.reggietout.filter;

import com.alibaba.fastjson.JSON;
import com.example.reggietout.tools.Result;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
@Slf4j
@WebFilter(filterName = "loginFilter",urlPatterns = "/*")
public class LoginFilter implements Filter {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();//路径匹配器
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();//本次请求
        log.info("拦截到请求：{}",uri);
        //不需要处理的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };

        boolean checkUrl = checkUrl(urls, uri);
        if (checkUrl){
            log.info("此请求无需处理：{}",uri);
            filterChain.doFilter(request,response);
            return;
        }


        //判断登录状态，之前登录的时候已经将id存入了redis
        if (stringRedisTemplate.opsForValue().get("user")!=null){
            log.info("已登录,id为 {}",stringRedisTemplate.opsForValue().get("user"));
            filterChain.doFilter(request,response);
            return;
        }


        log.info("未登录");
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
    }

    public boolean checkUrl(String[] urls,String requestUri){
        for (String url: urls) {
            boolean match = PATH_MATCHER.match(url, requestUri);
            if (match)
                return true;
        }
        return false;
    }
}
