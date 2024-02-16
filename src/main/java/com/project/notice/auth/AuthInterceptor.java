package com.project.notice.auth;

import com.project.notice.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;


    @Component
    public class AuthInterceptor implements HandlerInterceptor {

        @Autowired
        JwtUtil jwtUtil;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Method method = handlerMethod.getMethod();
                Auth auth = method.getAnnotation(Auth.class);

                if (auth == null) {
                    return true;
                }

                String token = request.getHeader("Authorization");


                if (token == null || token.isEmpty()) {
                    response.setStatus(401);
                    return false;
                }
                AuthProfile profile =
                        jwtUtil.validateToken(token.replace("Bearer ", ""));

                if (profile == null) {
                    System.out.println("not profile");
                    response.setStatus(401);
                    return false;
                }

                request.setAttribute("authProfile", profile);
                return true;
            }
            return true;
        }
    }
