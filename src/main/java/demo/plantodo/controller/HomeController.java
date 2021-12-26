package demo.plantodo.controller;

import demo.plantodo.domain.Plan;
import demo.plantodo.domain.PlanRegular;
import demo.plantodo.domain.TodoDate;
import demo.plantodo.form.CalendarSearchForm;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import demo.plantodo.service.CommentService;
import demo.plantodo.service.PlanService;
import demo.plantodo.service.TodoDateService;
import demo.plantodo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/home")
public class HomeController {
     private final MemberRepository memberRepository;
     private final PlanService planService;
     private final PlanRepository planRepository;
     private final TodoService todoService;
     private final TodoDateService todoDateService;
     private final CommentService commentService;

     @GetMapping
     public String createHome(HttpServletRequest request, HttpServletResponse response) {

          LocalDate today = LocalDate.now();
          if (!checkCookie(request)) {
               Cookie cookie = regularTodoDateInitiate(request, today);
               if (cookie.getName().equals("RegularTodoDateInitiatedToday")) {
                    response.addCookie(cookie);
               }
          }
          return "main-home";
     }

     @PostMapping
     public String afterSearchHome(@RequestParam("targetYear") int targetYear,
                                   @RequestParam("targetMonth") int targetMonth,
                                   Model model) {

          LocalDate tmpDate = LocalDate.of(targetYear, targetMonth, 1);
          int length = tmpDate.lengthOfMonth();
          CalendarSearchForm calendarSearchForm = new CalendarSearchForm(targetYear, targetMonth);
          LocalDate[][] calendar = calendarSearchForm.makeCalendar(targetYear, targetMonth, length);
          model.addAttribute("calendarSearchForm", calendarSearchForm);
          model.addAttribute("calendar", calendar);
          return "main-home";
     }

     @GetMapping("/calendar/{eachDate}")
     public String getDateBlock(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate eachDate, HttpServletRequest request, Model model) {
          boolean needUpdate = true;
          if (eachDate.isEqual(LocalDate.now())) {
               needUpdate = false;
          }
          Long memberId = memberRepository.getMemberId(request);
          List<Plan> plans = planRepository.findAllPlan(memberId);
          LinkedHashMap<Plan, List<TodoDate>> dateBlockData = new LinkedHashMap<>();
          for (Plan plan : plans) {
               List<TodoDate> planTodoDate = todoDateService.getTodoDateByDateAndPlan(plan, eachDate, needUpdate);
               dateBlockData.put(plan, planTodoDate);
          }
          model.addAttribute("selectedDate", eachDate);
          model.addAttribute("today", LocalDate.now());
          model.addAttribute("dateBlockData", dateBlockData);
          return "main-home :: #dateBlock";
     }

     public Cookie regularTodoDateInitiate(HttpServletRequest request, LocalDate today) {
          Long memberId = memberRepository.getMemberId(request);
          List<Plan> allPlan = planService.findAllPlan(memberId);

          /*plan이 하나라도 있으면 planRegular인지 확인*/
          if (!allPlan.isEmpty()) {
               for (Plan plan : allPlan) {
                    if (plan instanceof PlanRegular) {
                         todoDateService.getTodoDateByDateAndPlan(plan, today, true);
                    }
               }
               return makeInitiateCookie(today);
          }

          return new Cookie("notInitiate", "");
     }

     /*쿠키 생성*/
     public Cookie makeInitiateCookie(LocalDate today) {
          Cookie cookie = new Cookie("RegularTodoDateInitiatedToday", "initiated");
          LocalDateTime now = LocalDateTime.now();
          LocalDate tomorrow = today.plusDays(1);
          LocalDateTime expireMoment = LocalDateTime.of(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDayOfMonth(), 0, 0, 0);
          long expireRange = Duration.between(now, expireMoment).getSeconds();
          cookie.setMaxAge((int) expireRange);
          return cookie;
     }

     /*planRegular - todo의 오늘자 todoDate를 생성한 적이 있는지 확인*/
     public boolean checkCookie(HttpServletRequest request) {
          Cookie[] cookies = request.getCookies();
          for (Cookie cookie : cookies) {
               System.out.println("cookie.getName() = " + cookie.getName());
               if (cookie.getName().equals("RegularTodoDateInitiatedToday")) {
                    return true;
               }
          }
          return false;
     }

     public void deleteAllCookies(HttpServletRequest request) {
          Cookie[] cookies = request.getCookies();
          for (Cookie cookie : cookies) {
               cookie.setMaxAge(0);
          }
     }
}
