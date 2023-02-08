package demo.plantodo.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean loggedIn = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            log.info(cookies.toString());
            for (Cookie c : cookies) {
                if (c.getName().equals("AUTH")) {
                    log.info("AUTH sha-256 key : {}", c.getValue());
                    loggedIn = true;
                    break;
                }
            }
        }

        String requestURI = request.getRequestURI();
        if (!loggedIn) {
            response.sendRedirect("/member/login?redirectURL="+requestURI);
            return false;
        }
        return true;
    }
}
