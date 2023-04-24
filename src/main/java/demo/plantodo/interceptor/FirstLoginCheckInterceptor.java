package demo.plantodo.interceptor;

import demo.plantodo.domain.Settings;
import demo.plantodo.service.AuthService;
import demo.plantodo.service.CommonService;
import demo.plantodo.service.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class FirstLoginCheckInterceptor implements HandlerInterceptor {

    private final CommonService commonService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean loggedIn = false;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("AUTH")) {
                    log.info("AUTH sha-256 key : {}", c.getValue());
                    commonService.setDATCookie(c.getValue(), response);
                    loggedIn = true;
                    break;
                }
            }
        }

        if (loggedIn) {
            response.sendRedirect("/home");
            return false;
        }
        return true;
    }

}
