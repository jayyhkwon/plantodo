package demo.plantodo.interceptor;

import demo.plantodo.controller.HomeController;
import demo.plantodo.domain.Member;
import demo.plantodo.domain.Settings;
import demo.plantodo.form.CalendarSearchForm;
import demo.plantodo.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
public class HomeRenderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        beforeHome(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        beforeHome(request);
    }

    private void beforeHome(HttpServletRequest request) {
        /*캘린더*/
        LocalDate now = LocalDate.now();
        int yearValue = now.getYear();
        int monthValue = now.getMonthValue();
        int length = now.lengthOfMonth();
        CalendarSearchForm cSearchForm = new CalendarSearchForm(yearValue, monthValue);
        LocalDate[][] calendar = cSearchForm.makeCalendar(yearValue, monthValue, length);
        request.setAttribute("today", now);
        request.setAttribute("calendarSearchForm", cSearchForm);
        request.setAttribute("calendar", calendar);
    }
}
