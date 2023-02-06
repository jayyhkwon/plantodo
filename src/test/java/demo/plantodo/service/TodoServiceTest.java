package demo.plantodo.service;

import demo.plantodo.domain.*;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TodoServiceTest {

    @Autowired
    EntityManager em;

    @Autowired TodoService todoService;
    @Autowired MemberRepository memberRepository;
    @Autowired PlanRepository planRepository;

    @Test
    public void 일자별플랜별TodoDate가져오기_planRegular() throws Exception {
//        //given
//        Member member = new Member("test123@test.com", "12345678", "test");
//        memberRepository.save(member);
//
//        LocalDate startDate = LocalDate.of(2021, 12, 1);
//        PlanRegular planRegular = new PlanRegular(member, PlanStatus.NOW, startDate, "공부");
//        planRepository.saveRegular(planRegular);
//
//        ArrayList<String> repValue = new ArrayList<>();
//        repValue.add("3");
//
//        To-do to-do = new To-do(member, planRegular, "문제집 풀기", 3, repValue);
//        todoService.todoSave(to-do);
//
//        //when
//        List<TodoDate> result = todoService.getTodoDateByDate(planRegular, LocalDate.of(2021, 12, 19));
//
//        for (TodoDate todoDate : result) {
//            System.out.println("todoDate.getTodo().getTitle() = " + todoDate.getTodo().getTitle());
//        }
//        //then
//        Assertions.assertThat(result.size() == 1);
    }

    @Test
    public void 일자별플랜별TodoDate가져오기_planTerm_정상케이스() throws Exception {
//        //given
//        Member member = new Member("test123@test.com", "12345678", "test");
//        memberRepository.save(member);
//
//        LocalDate startDate = LocalDate.of(2021, 12, 1);
//        PlanRegular planRegular = new PlanRegular(member, PlanStatus.NOW, startDate, "공부");
//        planRepository.saveRegular(planRegular);
//
//        ArrayList<String> repValue = new ArrayList<>();
//        repValue.add("3");
//
//        To-do to-do = new To-do(member, planRegular, "문제집 풀기", 3, repValue);
//        todoService.todoSave(to-do);
//
//        //when
//        List<TodoDate> result = todoService.getTodoDateByDate(planRegular, LocalDate.of(2021, 12, 18));
//
//        //then
//        Assertions.assertThat(result.size() == 1);
    }

    @Test
    public void 일자별플랜별TodoDate가져오기_planTerm_에러케이스() throws Exception {
//        //given
//        Member member = new Member("test123@test.com", "12345678", "test");
//        memberRepository.save(member);
//
//        LocalDate startDate = LocalDate.of(2021, 12, 1);
//        LocalDate endDate = LocalDate.of(2021, 12, 18);
//        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, "공부", endDate);
//        planRepository.saveTerm(planTerm);
//
//        To-do to-do = new To-do(member, planTerm, "문제집 풀기", 0, null);
//        todoService.todoSave(to-do);
//
//        //when
//        List<TodoDate> result = todoService.getTodoDateByDate(planTerm, LocalDate.of(2021, 12, 19));
//
//        //then
//        Assertions.assertThat(result.isEmpty());
    }

    @Test
    public void saveTodoTest_planCount() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberRepository.save(member);

        /*plan 저장*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTerm plan = new PlanTerm(member, PlanStatus.NOW, start, "plan1", end);
        planRepository.saveTerm(plan);

        //when
        /*to-do 저장 (todoDate 자동 생성)*/
        Todo todo = new Todo(member, plan, "todo1", 0, null);
        todoService.save(plan, todo);

        //then
        Assertions.assertThat(todo.getPlan().getUnchecked_TodoDate_cnt()).isEqualTo(4);
    }

    @Test
    public void deleteTodoTest_planCount() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberRepository.save(member);

        /*plan 저장*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTerm plan = new PlanTerm(member, PlanStatus.NOW, start, "plan1", end);
        planRepository.saveTerm(plan);

        /*to-do 저장 (todoDate 자동 생성)*/
        Todo todo = new Todo(member, plan, "todo1", 0, null);
        todoService.save(plan, todo);

        System.out.println("[삭제 전 uncheckedCnt] " + planRepository.findOne(plan.getId()).getUnchecked_TodoDate_cnt());

        em.flush();
        em.clear();

        //when
        todoService.delete(todo.getId());

        //then
        Assertions.assertThat(planRepository.findOne(plan.getId()).getUnchecked_TodoDate_cnt()).isEqualTo(0);

    }
}