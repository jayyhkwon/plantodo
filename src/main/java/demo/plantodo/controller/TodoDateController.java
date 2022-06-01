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
        TodoDate one = todoDateService.findOne(todoDateId);
        if (one instanceof TodoDateRep) {
            todoDateService.deleteRep(todoDateId);
        } else {
            todoDateService.deleteDaily(todoDateId);
        }

        if (todoDateDeleteDataVO.getPageInfo().equals("home")) {
            TodoDateResHomeVO home = new TodoDateResHomeVO();
            home.setSearchDate(todoDateDeleteDataVO.getSelectedDate());
            home.setPageInfo("home");
            return home;
        } else {
            TodoDateResPlanVO plan = new TodoDateResPlanVO();
            plan.setPlanId(todoDateDeleteDataVO.getPlanId());
            plan.setPageInfo("plan");
            return plan;
        }
    }

    /*todoDate 상태변경*/
    @ResponseBody
    @PostMapping("/switching")
    public int switchStatus(@RequestParam Long todoDateId) {
        TodoDate todoDate = todoDateService.findOne(todoDateId);
        if (todoDate instanceof TodoDateRep) {
            todoDateService.switchStatusRep(todoDateId);
            Plan plan = ((TodoDateRep) todoDate).getTodo().getPlan();
            return plan.calculate_plan_compPercent();
        } else {
            todoDateService.switchStatusDaily(todoDateId);
            Plan plan = ((TodoDateDaily) todoDate).getPlan();
            return plan.calculate_plan_compPercent();
        }
    }

    @PostMapping("/daily")
    @ResponseBody
    public TodoDateDailyOutputVO registerTodoDateDaily(@ModelAttribute TodoDateDailyInputVO todoDateDailyInputVO) {
        Plan plan = planService.findOne(todoDateDailyInputVO.getPlanId());
        TodoDate todoDate = new TodoDateDaily(TodoStatus.UNCHECKED, todoDateDailyInputVO.getSelectedDailyDate(), todoDateDailyInputVO.getTitle(), plan);
        todoDateService.save(todoDate);

        Long todoDateId = todoDate.getId();
        TodoDateDailyOutputVO outputVO = new TodoDateDailyOutputVO();
        outputVO.setTodoDateId(todoDateId);
        outputVO.setSearchDate(todoDateDailyInputVO.getSelectedDailyDate());

        return outputVO;
    }

    @PutMapping
    @ResponseBody
    public Object updateTodoDate(@ModelAttribute TodoDateUpdateDataVO todoDateUpdateDataVO) {
        todoDateService.updateTitle(todoDateUpdateDataVO.getTodoDateId(), todoDateUpdateDataVO.getUpdateTitle());
        if (todoDateUpdateDataVO.getPageInfo().equals("home")) {
            TodoDateResHomeVO home = new TodoDateResHomeVO();
            home.setPageInfo(todoDateUpdateDataVO.getPageInfo());
            home.setSearchDate(todoDateUpdateDataVO.getSelectedDate());
            return home;
        } else {
            TodoDateResPlanVO plan = new TodoDateResPlanVO();
            plan.setPageInfo(todoDateUpdateDataVO.getPageInfo());
            plan.setPlanId(todoDateUpdateDataVO.getPlanId());
            return plan;
        }
    }
}
