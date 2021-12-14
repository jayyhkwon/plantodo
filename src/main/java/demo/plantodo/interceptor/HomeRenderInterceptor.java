package demo.plantodo.interceptor;

import demo.plantodo.controller.HomeController;
import demo.plantodo.form.CalendarSearchForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@Slf4j
public class HomeRenderInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("[HomeRenderInterceptor preHandle] [requestURI] " + requestURI);
        beforeHome(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("[HomeRenderInterceptor postHandle] [requestURI] " + requestURI);
        beforeHome(request);
    }

    private void beforeHome(HttpServletRequest request) {
        LocalDate now = LocalDate.now();
        int yearValue = now.getYear();
        int monthValue = now.getMonthValue();
        int length = now.lengthOfMonth();
        CalendarSearchForm cSearchForm = new CalendarSearchForm(yearValue, monthValue);
        LocalDate[][] calendar = cSearchForm.makeCalendar(yearValue, monthValue, length);

        request.setAttribute("calendarSearchForm", cSearchForm);
        request.setAttribute("calendar", calendar);
    }
}
