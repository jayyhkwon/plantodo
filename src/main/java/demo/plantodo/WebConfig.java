package demo.plantodo;

import demo.plantodo.interceptor.HomeRenderInterceptor;
import demo.plantodo.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/member/join", "/member/login");

        registry.addInterceptor(new HomeRenderInterceptor())
                .order(2)
                .addPathPatterns("/home", "/home/*", "/member/login", "/todo/register", "/plan/register/*");

    }
}
