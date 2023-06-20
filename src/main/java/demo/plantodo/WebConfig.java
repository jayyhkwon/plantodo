package demo.plantodo;

import demo.plantodo.converter.StringToEnumConverterFactory;
import demo.plantodo.interceptor.FirstLoginCheckInterceptor;
import demo.plantodo.interceptor.HomeRenderInterceptor;
import demo.plantodo.interceptor.LoginCheckInterceptor;
import demo.plantodo.service.*;
import demo.plantodo.service.impl.ConcurrentServiceImpl;
import demo.plantodo.service.impl.ConcurrentStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CommonService commonService;
//    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(firstLoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/");

        registry.addInterceptor(loginCheckInterceptor())
                .order(2)
                .addPathPatterns("/home/**", "/plan/**", "/comment/**", "/settings/**", "/sse/**", "/todo/**", "/todoDate/**")
                        .excludePathPatterns("/member/**");

        registry.addInterceptor(homeRenderInterceptor())
                .order(3)
                .addPathPatterns("/home", "/home/*", "/member/login", "/todo/register", "/plan/register/*")
                .excludePathPatterns("/", "/member/join");
    }

    @Bean
    public HomeRenderInterceptor homeRenderInterceptor() {
        return new HomeRenderInterceptor();
    }

    @Bean
    public LoginCheckInterceptor loginCheckInterceptor() {
        return new LoginCheckInterceptor(commonService);
    }

    @Bean
    public FirstLoginCheckInterceptor firstLoginCheckInterceptor() {
        return new FirstLoginCheckInterceptor(commonService);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToEnumConverterFactory());
    }

    @Bean
    public ConcurrentStore concurrentStore() {
        return new ConcurrentStore();
    }

    @Bean
    public CacheService cacheService() {
//        return new RedisServiceImpl(redisTemplate);
        return new ConcurrentServiceImpl(concurrentStore().getStore());
    }
}

