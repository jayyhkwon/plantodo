package demo.plantodo.controller;

import demo.plantodo.domain.TodoDate;
import demo.plantodo.domain.TodoDateComment;
import demo.plantodo.service.CommentService;
import demo.plantodo.service.TodoDateService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/todoDate")
public class TodoDateController {
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
        todoDateService.delete(todoDateId);

        String redirectURI = "/plan/" + planId;
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);
        redirectView.setUrl(redirectURI);
        return redirectView;
    }

    /*todoDate 상태변경*/
    @ResponseBody
    @PostMapping("/switching")
    public boolean switchStatus(@RequestParam Long todoDateId) {
        todoDateService.switchStatus(todoDateId);
        return true;
    }
}
