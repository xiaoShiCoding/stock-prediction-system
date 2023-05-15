package com.stock.interceptor;

import com.stock.util.JwtUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.stock.config.WebConfig.PREFLIGHT;

/**
 * @author admin
 * @description 用户检验token
 * @since 2023-03-31
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (PREFLIGHT.equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (token == null) {
            return false;
        }
        token = token.replaceAll("\"", "");
        try {
            jwtUtils.verify(token);
        } catch (Exception e) {
            return false;
        }

        String username = request.getHeader("username");
        if (username == null) {
            return false;
        }
        username = username.replaceAll("\"", "");
        String s = stringRedisTemplate.opsForValue().get(username);
        return token.equals(s);
    }
}
