package demo.plantodo.service;


import demo.plantodo.PlantodoApplication;
import demo.plantodo.domain.*;
import demo.plantodo.form.PlanTermUpdateForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.events.Comment;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Transactional
@SpringBootTest(classes = {PlantodoApplication.class})
public class PlanServiceTest_Edit {
    @Autowired private PlanService planService;
    @Autowired private MemberService memberService;
    @Autowired private TodoDateService todoDateService;
    @Autowired private TodoService todoService;
    @Autowired private CommentService commentService;


    @Test
    @DisplayName("todo 한 개(매일)")
    public void planTermEdit_del() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        LocalDate startDate = LocalDate.of(2023, 5, 31);
        LocalDate endDate = startDate.plusDays(7);

        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, "test", endDate);
        planService.saveTerm(planTerm);

        Todo todo = new Todo(member, planTerm, "todo", 0, new ArrayList<>());
        todoService.save(planTerm, todo);

        //when
        LocalDate newEndDate = startDate.plusDays(1);
        PlanTermUpdateForm form = new PlanTermUpdateForm("test_rev", startDate, newEndDate, "23:59");
        planService.updateTerm_del(form, newEndDate, endDate, planTerm.getId());

        //then
        Todo newTodo = todoService.findOne(todo.getId());
        List<TodoDate> tdd_list = todoDateService.getTodoDateByTodo(newTodo);
        Assertions.assertThat(tdd_list.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("todo 한 개(매일), daily 있음(매일)")
    public void planTermEdit_del_2() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        LocalDate startDate = LocalDate.of(2023, 5, 31);
        LocalDate endDate = startDate.plusDays(7);

        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, "test", endDate);
        planService.saveTerm(planTerm);

        Todo todo = new Todo(member, planTerm, "todo", 0, new ArrayList<>());
        todoService.save(planTerm, todo);

        Iterator<LocalDate> iterator = Stream.iterate(startDate, d -> d.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate)+1).iterator();
        while (iterator.hasNext()) {
            LocalDate dk = iterator.next();
            TodoDateDaily tddd = new TodoDateDaily(TodoStatus.UNCHECKED, dk, "tdd", planTerm);
            todoDateService.save(tddd);
        }

        //when
        LocalDate newEndDate = startDate.plusDays(1);
        PlanTermUpdateForm form = new PlanTermUpdateForm("test_rev", startDate, newEndDate, "23:59");
        planService.updateTerm_del(form, newEndDate, endDate, planTerm.getId());

        //then
        Todo newTodo = todoService.findOne(todo.getId());

        List<TodoDate> tddr_list = todoDateService.getTodoDateByTodo(newTodo);
        List<TodoDateDaily> tddd_list = todoDateService.getTodoDateDailyByPlanId(planTerm.getId());

        Assertions.assertThat(tddr_list.size()+tddd_list.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("todo 한 개(매일), daily 있음(이틀에 한 번)")
    public void planTermEdit_del_3() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        LocalDate startDate = LocalDate.of(2023, 5, 31);
        LocalDate endDate = startDate.plusDays(7);

        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, "test", endDate);
        planService.saveTerm(planTerm);

        Todo todo = new Todo(member, planTerm, "todo", 0, new ArrayList<>());
        todoService.save(planTerm, todo);

        Iterator<LocalDate> iterator = Stream.iterate(startDate, d -> d.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate)+1).iterator();
        while (iterator.hasNext()) {
            iterator.next();
            if (!iterator.hasNext()) break;
            LocalDate dk = iterator.next();
            TodoDateDaily tddd = new TodoDateDaily(TodoStatus.UNCHECKED, dk, "tdd", planTerm);
            todoDateService.save(tddd);
        }

        //when
        LocalDate newEndDate = startDate.plusDays(3);
        PlanTermUpdateForm form = new PlanTermUpdateForm("test_rev", startDate, newEndDate, "23:59");
        planService.updateTerm_del(form, newEndDate, endDate, planTerm.getId());

        //then
        Todo newTodo = todoService.findOne(todo.getId());

        List<TodoDate> tddr_list = todoDateService.getTodoDateByTodo(newTodo);
        List<TodoDateDaily> tddd_list = todoDateService.getTodoDateDailyByPlanId(planTerm.getId());

        Assertions.assertThat(tddr_list.size()+tddd_list.size()).isEqualTo(6);
    }

    @Test
    @DisplayName("todo 한 개(월 수 금), daily 없음")
    public void planTermEdit_del_4() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        LocalDate startDate = LocalDate.of(2023, 5, 31);
        LocalDate endDate = startDate.plusDays(7);

        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, "test", endDate);
        planService.saveTerm(planTerm);

        List<String> repValue = new ArrayList<>();
        repValue.add("월");
        repValue.add("수");
        repValue.add("금");
        Todo todo = new Todo(member, planTerm, "todo", 1, repValue);
        todoService.save(planTerm, todo);

        //when
        LocalDate newEndDate = startDate.plusDays(3);
        PlanTermUpdateForm form = new PlanTermUpdateForm("test_rev", startDate, newEndDate, "23:59");
        planService.updateTerm_del(form, newEndDate, endDate, planTerm.getId());

        //then
        Todo newTodo = todoService.findOne(todo.getId());

        List<TodoDate> tddr_list = todoDateService.getTodoDateByTodo(newTodo);
        Assertions.assertThat(tddr_list.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("todo 한 개(3일마다), daily 없음")
    public void planTermEdit_del_5() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        LocalDate startDate = LocalDate.of(2023, 5, 31);
        LocalDate endDate = startDate.plusDays(7);

        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, "test", endDate);
        planService.saveTerm(planTerm);

        List<String> repValue = new ArrayList<>();
        repValue.add("3");
        Todo todo = new Todo(member, planTerm, "todo", 2, repValue);
        todoService.save(planTerm, todo);

        //when
        LocalDate newEndDate = startDate.plusDays(3);
        PlanTermUpdateForm form = new PlanTermUpdateForm("test_rev", startDate, newEndDate, "23:59");
        planService.updateTerm_del(form, newEndDate, endDate, planTerm.getId());

        //then
        Todo newTodo = todoService.findOne(todo.getId());

        List<TodoDate> tddr_list = todoDateService.getTodoDateByTodo(newTodo);
        Assertions.assertThat(tddr_list.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("todo 한 개(월 수 금), daily 없음, comment 있음")
    public void planTermEdit_del_6() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        LocalDate startDate = LocalDate.of(2023, 5, 31);
        LocalDate endDate = startDate.plusDays(7);

        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, "test", endDate);
        planService.saveTerm(planTerm);

        List<String> repValue = new ArrayList<>();
        repValue.add("월");
        repValue.add("수");
        repValue.add("금");
        Todo todo = new Todo(member, planTerm, "todo", 1, repValue);
        todoService.save(planTerm, todo);

        List<TodoDate> todos = todoDateService.getTodoDateByTodo(todo);
        for (TodoDate tdd : todos) {
            commentService.save(tdd.getId(), "comment");
        }

        //when
        LocalDate newEndDate = startDate.plusDays(3);
        PlanTermUpdateForm form = new PlanTermUpdateForm("test_rev", startDate, newEndDate, "23:59");
        planService.updateTerm_del(form, newEndDate, endDate, planTerm.getId());

        //then
        Todo newTodo = todoService.findOne(todo.getId());

        int commentCnt = 0;
        for (TodoDate tdd : todos) {
            commentCnt += commentService.getCommentsByTodoDateId(tdd.getId()).size();
        }

        List<TodoDate> tddr_list = todoDateService.getTodoDateByTodo(newTodo);
        Assertions.assertThat(commentCnt).isEqualTo(2);
    }

    @Test
    @DisplayName("todo 1개(매일), daily 없음")
    public void planTermEdit_add_1() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        LocalDate startDate = LocalDate.of(2023, 5, 31);
        LocalDate endDate = startDate.plusDays(7);

        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, "test", endDate);
        planService.saveTerm(planTerm);

        Todo todo = new Todo(member, planTerm, "todo", 0, new ArrayList<>());
        todoService.save(planTerm, todo);

        //when
        LocalDate newEndDate = endDate.plusDays(1);
        PlanTermUpdateForm form = new PlanTermUpdateForm("test_rev", startDate, newEndDate, "23:59");
        planService.updateTerm_add(form, newEndDate, endDate, planTerm.getId());

        //then
        Todo newTodo = todoService.findOne(todo.getId());
        List<TodoDate> tddr_list = todoDateService.getTodoDateByTodo(newTodo);
        Assertions.assertThat(tddr_list.size()).isEqualTo(9);
    }

    @Test
    @DisplayName("todo 1개(월 수 금), daily 없음")
    public void planTermEdit_add_2() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        LocalDate startDate = LocalDate.of(2023, 5, 31);
        LocalDate endDate = startDate.plusDays(7);

        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, "test", endDate);
        planService.saveTerm(planTerm);

        List<String> repValue = new ArrayList<>();
        repValue.add("월");
        repValue.add("수");
        repValue.add("금");

        Todo todo = new Todo(member, planTerm, "todo", 1, repValue);
        todoService.save(planTerm, todo);

        //when
        LocalDate newEndDate = endDate.plusDays(3);
        PlanTermUpdateForm form = new PlanTermUpdateForm("test_rev", startDate, newEndDate, "23:59");
        planService.updateTerm_add(form, newEndDate, endDate, planTerm.getId());

        //then
        Todo newTodo = todoService.findOne(todo.getId());
        List<TodoDate> tddr_list = todoDateService.getTodoDateByTodo(newTodo);
        Assertions.assertThat(tddr_list.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("todo 1개(2일마다), daily 없음")
    public void planTermEdit_add_3() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        LocalDate startDate = LocalDate.of(2023, 5, 31);
        LocalDate endDate = startDate.plusDays(7);

        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, "test", endDate);
        planService.saveTerm(planTerm);

        List<String> repValue = new ArrayList<>();
        repValue.add("2");

        Todo todo = new Todo(member, planTerm, "todo", 2, repValue);
        todoService.save(planTerm, todo);

        //when
        LocalDate newEndDate = endDate.plusDays(3);
        PlanTermUpdateForm form = new PlanTermUpdateForm("test_rev", startDate, newEndDate, "23:59");
        planService.updateTerm_add(form, newEndDate, endDate, planTerm.getId());

        //then
        Todo newTodo = todoService.findOne(todo.getId());
        List<TodoDate> tddr_list = todoDateService.getTodoDateByTodo(newTodo);
        Assertions.assertThat(tddr_list.size()).isEqualTo(6);
    }
}
