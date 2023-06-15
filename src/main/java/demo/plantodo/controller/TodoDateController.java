package demo.plantodo.controller;

import demo.plantodo.VO.*;
import demo.plantodo.domain.*;
import demo.plantodo.service.CommentService;
import demo.plantodo.service.MemberService;
import demo.plantodo.service.PlanService;
import demo.plantodo.service.TodoDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/todoDate")
public class TodoDateController {
    private final PlanService planService;
    private final TodoDateService todoDateService;
    private final CommentService commentService;

    /*todoDate 상세조회*/
    @GetMapping
    public String getTodoDateDetailBlock(@RequestParam Long todoDateId,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate,
                                         Model model) {
        TodoDateSimpleVO todoDate = new TodoDateSimpleVO(todoDateService.findOne(todoDateId).getId());
        List<CommentVO> comments = commentService.getCommentVOByTodoDateId(todoDateId);
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

        todoDateService.delete(todoDateId);

        if (todoDateDeleteDataVO.getPageInfo().equals("home")) {
            return new TodoDateResHomeVO("home", todoDateDeleteDataVO.getSelectedDate());
        } else {
            return new TodoDateResPlanVO("plan", todoDateDeleteDataVO.getPlanId());
        }
    }

    /*todoDate 상태변경*/
    @ResponseBody
    @PostMapping("/switching")
    public int switchStatus(@RequestParam Long todoDateId, @RequestParam Long planId) {
        TodoDate todoDate = todoDateService.findOne(todoDateId);
        if (todoDate instanceof TodoDateRep) {
            todoDateService.switchStatusRep(todoDateId);
            Plan plan = planService.findOne(planId);
            return plan.calculate_plan_compPercent();
        } else {
            todoDateService.switchStatusDaily(todoDateId);
            Plan plan = planService.findOne(planId);
            return plan.calculate_plan_compPercent();
        }
    }

    @PostMapping("/daily")
    @ResponseBody
    public TodoDateDailyOutputVO registerTodoDateDaily(@ModelAttribute TodoDateDailyInputVO todoDateDailyInputVO) {
        Plan plan = planService.findOne(todoDateDailyInputVO.getPlanId());
        TodoDate todoDate = new TodoDateDaily(TodoStatus.UNCHECKED, todoDateDailyInputVO.getSelectedDailyDate(), todoDateDailyInputVO.getTitle(), plan);
        todoDateService.save(todoDate);

        return new TodoDateDailyOutputVO(todoDateDailyInputVO.getSelectedDailyDate(), todoDate.getId());
    }

    @PutMapping
    @ResponseBody
    public Object updateTodoDate(@ModelAttribute TodoDateUpdateDataVO todoDateUpdateDataVO) {
        todoDateService.updateTitle(todoDateUpdateDataVO.getTodoDateId(), todoDateUpdateDataVO.getUpdateTitle());
        if (todoDateUpdateDataVO.getPageInfo().equals("home")) {
            return new TodoDateResHomeVO("home", todoDateUpdateDataVO.getSelectedDate());
        } else {
            return new TodoDateResPlanVO("plan", todoDateUpdateDataVO.getPlanId());
        }
    }
}
