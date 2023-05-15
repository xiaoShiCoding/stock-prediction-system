package com.stock.interceptor;

import com.stock.util.DealUserValid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.stock.config.WebConfig.PREFLIGHT;

/**
 * @author admin
 * @description 用户验证身份合法, 该拦截器在本系统并未起到实际作用, 在实际使用中, 该拦截器是用于判断用户身份, 防止用户请求别人的私密信息, 方便进行服务层代码的编写
 * @since 2023-03-31
 */
@Component
public class UserValidInterceptor implements HandlerInterceptor {
    @Autowired
    DealUserValid dealUserValid;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (PREFLIGHT.equals(request.getMethod())) {
            return true;
        }

        String username = request.getHeader("username");
        String password = request.getHeader("password");
        if (username == null || password == null) {
            return false;
        }
        username = username.replaceAll("\"", "");
        password = password.replaceAll("\"", "");

        if (!dealUserValid.judgeUserValid(username, password)) {
            try {
                response.getWriter().println("Authentication Error");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return true;
    }
}
