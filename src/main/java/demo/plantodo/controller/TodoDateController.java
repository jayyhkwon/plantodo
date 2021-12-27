package demo.plantodo.controller;

import demo.plantodo.domain.*;
import demo.plantodo.service.CommentService;
import demo.plantodo.service.MemberService;
import demo.plantodo.service.PlanService;
import demo.plantodo.service.TodoDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/todoDate")
public class TodoDateController {
    private final PlanService planService;
    private final MemberService memberService;
    private final TodoDateService todoDateService;
    private final CommentService commentService;

    /*todoDate 상세조회*/
    @GetMapping
    public String getTodoDateDetailBlock(@RequestParam Long todoDateId,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate,
                                         Model model) {
        TodoDate todoDate = todoDateService.findOne(todoDateId);
        List<TodoDateComment> comments = commentService.getCommentsByTodoDateId(todoDateId);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("comments", comments);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("todoDate", todoDate);
        return "fragments/todoDate-detail-block :: todoDateDetailList";
    }

    /*todoDate 삭제*/
    @DeleteMapping
    public RedirectView deleteTodoDate(@RequestParam Long planId, @RequestParam Long todoDateId, RedirectView redirectView) {
        TodoDate one = todoDateService.findOne(todoDateId);
        if (one instanceof TodoDateRep) {
            todoDateService.deleteRep(todoDateId);
        } else {
            todoDateService.deleteDaily(todoDateId);
        }
        String redirectURI = "/plan/" + planId;
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);
        redirectView.setUrl(redirectURI);
        return redirectView;
    }

    /*todoDate 상태변경*/
    @ResponseBody
    @PostMapping("/switching")
    public boolean switchStatus(@RequestParam Long todoDateId) {
        TodoDate one = todoDateService.findOne(todoDateId);
        if (one instanceof TodoDateRep) {
            todoDateService.switchStatusRep(todoDateId);
        } else {
            todoDateService.switchStatusDaily(todoDateId);
        }
        return true;
    }

    @PostMapping("/daily")
    public String registerTodoDateDaily(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate,
                                      @RequestParam Long planId,
                                      @RequestParam String title,
                                      HttpServletRequest request) {
        Plan plan = planService.findOne(planId);
        TodoDate todoDate = new TodoDateDaily(TodoStatus.UNCHECKED, selectedDate, title, plan);
        todoDateService.save(todoDate);

        return "redirect:/home/calendar/" + selectedDate;
    }
}
