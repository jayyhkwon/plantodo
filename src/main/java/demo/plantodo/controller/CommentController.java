package demo.plantodo.controller;

import demo.plantodo.domain.TodoDate;
import demo.plantodo.domain.TodoDateComment;
import demo.plantodo.service.CommentService;
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
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final TodoDateService todoDateService;

    /*comment 등록*/
    @PostMapping
    public String registerComment(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate,
                                  @RequestParam Long todoDateId,
                                  @RequestParam String comment,
                                  Model model) {

        commentService.save(todoDateId, comment);

        TodoDate todoDate = todoDateService.findOne(todoDateId);
        List<TodoDateComment> comments = commentService.getCommentsByTodoDateId(todoDateId);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("comments", comments);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("todoDate", todoDate);
        return "fragments/todoDate-detail-block :: todoDateDetailList";
    }

    /*comment 삭제*/
    @DeleteMapping
    public String deleteComment(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate,
                                @RequestParam Long commentId,
                                @RequestParam Long todoDateId,
                                Model model) {
        commentService.delete(commentId);
        TodoDate todoDate = todoDateService.findOne(todoDateId);
        List<TodoDateComment> comments = commentService.getCommentsByTodoDateId(todoDateId);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("comments", comments);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("todoDate", todoDate);
        return "fragments/todoDate-detail-block :: todoDateDetailList";
    }

    /*comment 수정*/
    @ResponseBody
    @PutMapping
    public void updateComment(@RequestParam Long commentId,
                              @RequestParam String updatedComment) {
        commentService.update(commentId, updatedComment);
    }
}
