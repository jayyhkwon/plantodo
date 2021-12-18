package demo.plantodo.controller;

import demo.plantodo.domain.Plan;
import demo.plantodo.domain.Todo;
import demo.plantodo.domain.TodoDate;
import demo.plantodo.form.CalendarSearchForm;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import demo.plantodo.repository.TodoRepository;
import demo.plantodo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/home")
public class HomeController {
     private final MemberRepository memberRepository;
     private final PlanRepository planRepository;
     private final TodoService todoService;

     @GetMapping
     public String createHome(Model model, HttpServletRequest request) {
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
          Long memberId = memberRepository.getMemberId(request);
          List<Plan> plans = planRepository.findAllPlan(memberId);
          LinkedHashMap<Plan, List<TodoDate>> dateBlockData = new LinkedHashMap<>();
          for (Plan plan : plans) {
               List<TodoDate> planTodoDate = todoService.getTodoDateByDateAndPlan(plan, eachDate);
               dateBlockData.put(plan, planTodoDate);
          }
          model.addAttribute("selectedDate", eachDate);
          model.addAttribute("today", LocalDate.now());
          model.addAttribute("dateBlockData", dateBlockData);
          return "main-home :: #dateBlock";
     }

     @PostMapping("/calendar/todoDate/switching")
     public String switchStatus(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate,
                                @RequestParam Long todoDateId) {
          todoService.switchStatus(todoDateId);
          String redirectURI = "redirect:/home/calendar/" + selectedDate;
          return redirectURI;
     }
}
