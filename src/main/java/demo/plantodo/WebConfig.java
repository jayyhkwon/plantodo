package demo.plantodo;

import demo.plantodo.converter.StringToEnumConverterFactory;
import demo.plantodo.interceptor.FirstLoginCheckInterceptor;
import demo.plantodo.interceptor.HomeRenderInterceptor;
import demo.plantodo.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FirstLoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/home/**", "/plan/**", "/comment/**", "/settings/**", "/sse/**", "/todo/**", "/todoDate/**")
                        .excludePathPatterns("/member/**");

        registry.addInterceptor(new HomeRenderInterceptor())
                .order(3)
                .addPathPatterns("/home", "/home/*", "/member/login", "/todo/register", "/plan/register/*")
                .excludePathPatterns("/", "/member/join");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToEnumConverterFactory());
    }
}
