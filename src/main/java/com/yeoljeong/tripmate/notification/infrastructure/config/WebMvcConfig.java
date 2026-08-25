package com.yeoljeong.tripmate.notification.infrastructure.config;

import com.yeoljeong.tripmate.auth.context.LoginUserArgumentResolver;
import com.yeoljeong.tripmate.auth.context.UserContextInterceptor;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component("customWebMvcConfig")
@Primary
public class WebMvcConfig implements WebMvcConfigurer {

  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new UserContextInterceptor()).excludePathPatterns(
        "/auth/login", "/auth/refresh", "/users/signup", "/internal/**", "/actuator/**");
  }

  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new LoginUserArgumentResolver());
  }
}
