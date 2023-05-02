package demo.plantodo.interceptor;

import demo.plantodo.service.CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {

    private final CommonService commonService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean loggedIn = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("AUTH")) {
                    loggedIn = true;
                    commonService.setDATCookie(c.getValue(), response);
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
