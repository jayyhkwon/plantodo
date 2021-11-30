package demo.plantodo.controller;

import demo.plantodo.form.CalendarSearchForm;
import demo.plantodo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class HomeController {
     private final MemberRepository memberRepository;

     @GetMapping("/home")
     public String createHome(Model model, HttpServletRequest request) {
          Long memberId = memberRepository.getMemberId(request);
          if (memberId == null) {
               return "index";
          }
          beforeHome(model);
          return "main-home";
     }

     @PostMapping("/home")
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

     public void beforeHome(Model model) {
          LocalDate now = LocalDate.now();
          int yearValue = now.getYear();
          int monthValue = now.getMonthValue();
          int length = now.lengthOfMonth();
          CalendarSearchForm cSearchForm = new CalendarSearchForm(yearValue, monthValue);
          LocalDate[][] calendar = cSearchForm.makeCalendar(yearValue, monthValue, length);

          model.addAttribute("calendarSearchForm", cSearchForm);
          model.addAttribute("calendar", calendar);
     }
}
