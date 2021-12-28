package demo.plantodo.controller;

import demo.plantodo.VO.*;
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
    @ResponseBody
    public Object deleteTodoDate(@ModelAttribute TodoDateDeleteDataVO todoDateDeleteDataVO) {
        Long todoDateId = todoDateDeleteDataVO.getTodoDateId();
        System.out.println("todoDateId = " + todoDateId);
        TodoDate one = todoDateService.findOne(todoDateId);
        if (one instanceof TodoDateRep) {
            todoDateService.deleteRep(todoDateId);
        } else {
            todoDateService.deleteDaily(todoDateId);
        }
        if (todoDateDeleteDataVO.getPageInfo().equals("home")) {
            TodoDateDeleteResHomeVO home = new TodoDateDeleteResHomeVO();
            home.setSearchDate(todoDateDeleteDataVO.getSelectedDate());
            home.setPageInfo("home");
            return home;
        } else {
            TodoDateDeleteResPlanVO plan = new TodoDateDeleteResPlanVO();
            plan.setPlanId(todoDateDeleteDataVO.getPlanId());
            plan.setPageInfo("plan");
            return plan;
        }
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
    @ResponseBody
    public TodoDateDailyOutputVO registerTodoDateDaily(@ModelAttribute TodoDateDailyInputVO todoDateDailyInputVO,
                                      HttpServletRequest request) {
        System.out.println("todoDateDailyInputVO.getPlanId() = " + todoDateDailyInputVO.getPlanId());
        Plan plan = planService.findOne(todoDateDailyInputVO.getPlanId());
        TodoDate todoDate = new TodoDateDaily(TodoStatus.UNCHECKED, todoDateDailyInputVO.getSelectedDailyDate(), todoDateDailyInputVO.getTitle(), plan);
        todoDateService.save(todoDate);
        Long todoDateId = todoDate.getId();
        TodoDateDailyOutputVO outputVO = new TodoDateDailyOutputVO();
        outputVO.setTodoDateId(todoDateId);
        outputVO.setSearchDate(todoDateDailyInputVO.getSelectedDailyDate());
        return outputVO;
    }
}
