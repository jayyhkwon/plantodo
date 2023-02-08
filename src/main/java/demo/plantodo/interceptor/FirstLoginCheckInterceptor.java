package demo.plantodo.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class FirstLoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean loggedIn = false;
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("AUTH")) {
                log.info("AUTH sha-256 key : {}", c.getValue());
                loggedIn = true;
                break;
            }
        }
        if (loggedIn) {
            response.sendRedirect("/home?redirectURL=" + request.getRequestURI());
            return false;
        }
        return true;
    }
}
