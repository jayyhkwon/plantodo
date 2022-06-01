package demo.plantodo.service;

import demo.plantodo.domain.*;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import demo.plantodo.repository.TodoDateRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Transactional
class TodoDateServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private TodoDateRepository todoDateRepository;

    @Autowired
    private TodoDateService todoDateService;

    @Autowired
    private TodoService todoService;

    @Test
    public void todoDateSave_Test() throws Exception {
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
        /*todoDate 저장*/
        TodoDateDaily todoDate = new TodoDateDaily(TodoStatus.UNCHECKED, start, "todoDate1", plan);
        todoDateService.save(todoDate);

        //then
        Assertions.assertThat(plan.getUnchecked_TodoDate_cnt()).isEqualTo(1);
    }

    @Test
    public void getTodoDateByDateAndPlan_Test() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberRepository.save(member);

        /*plan 저장*/
        LocalDate start = LocalDate.now().plusDays(1);
        PlanRegular plan = new PlanRegular(member, PlanStatus.NOW, start, "plan1");
        planRepository.saveRegular(plan);

        /*To-do 저장*/
        Todo todo = new Todo(member, plan, "todo1", 0, null);
        todoService.save(plan, todo);

        //when
        todoDateService.getTodoDateByDateAndPlan(plan, start, true);

        //then
        Assertions.assertThat(planRepository.findOne(plan.getId()).getUnchecked_TodoDate_cnt()).isEqualTo(1);
    }


    @Test
    public void switchDaily_Test() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberRepository.save(member);

        /*plan 저장*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTerm plan = new PlanTerm(member, PlanStatus.NOW, start, "plan1", end);
        planRepository.saveTerm(plan);

        /*todoDate 저장*/
        TodoDateDaily todoDate = new TodoDateDaily(TodoStatus.UNCHECKED, start, "todoDate1", plan);
        todoDateService.save(todoDate);

        System.out.println("[변경 전 uncheckedCnt] : " + planRepository.findOne(plan.getId()).getUnchecked_TodoDate_cnt());

        //when
        /*todoDate 상태 변경*/
        todoDateService.switchStatusDaily(todoDate.getId());

        //then
        Assertions.assertThat(planRepository.findOne(plan.getId()).getChecked_TodoDate_cnt()).isEqualTo(1);
        Assertions.assertThat(planRepository.findOne(plan.getId()).getUnchecked_TodoDate_cnt()).isEqualTo(0);

    }

    @Test
    public void switchRep_Test() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberRepository.save(member);

        /*plan 저장*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTerm plan = new PlanTerm(member, PlanStatus.NOW, start, "plan1", end);
        planRepository.saveTerm(plan);

        /*To-do 저장*/
        Todo todo = new Todo(member, plan, "todo1", 0, null);
        todoService.save(plan, todo);

        //when
        List<TodoDate> result_list = todoDateRepository.getTodoDateRep_ByTodoAndDate(todo, start);

        System.out.println(result_list.size());

        for (TodoDate todoDate : result_list) {
            todoDateService.switchStatusRep(todoDate.getId());
        }

        //then
        Assertions.assertThat(todo.getPlan().getChecked_TodoDate_cnt()).isEqualTo(1);
    }


    @Test
    public void deleteDaily_Test() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberRepository.save(member);

        /*plan 저장*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTerm plan = new PlanTerm(member, PlanStatus.NOW, start, "plan1", end);
        planRepository.saveTerm(plan);

        /*todoDate 저장*/
        TodoDateDaily todoDate = new TodoDateDaily(TodoStatus.UNCHECKED, start, "todoDate1", plan);
        todoDateRepository.save(todoDate);

        //when
        todoDateRepository.deleteDaily(todoDate);

        //then
        Assertions.assertThat(planRepository.findOne(plan.getId()).getUnchecked_TodoDate_cnt()).isEqualTo(0);
    }

    @Test
    public void deleteRep_Test() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberRepository.save(member);

        /*plan 저장*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTerm plan = new PlanTerm(member, PlanStatus.NOW, start, "plan1", end);
        planRepository.saveTerm(plan);

        /*To-do 저장*/
        Todo todo = new Todo(member, plan, "todo1", 0, null);
        todoService.save(plan, todo);

        //when
        /*TodoDate 조회 및 삭제 (1개)*/
        List<TodoDate> result = todoDateRepository.getTodoDateRep_ByTodoAndDate(todo, start);
        for (TodoDate todoDate : result) {
            todoDateService.deleteRep(todoDate.getId());
        }

        //then
        Assertions.assertThat(planRepository.findOne(plan.getId()).getUnchecked_TodoDate_cnt()).isEqualTo(3);
    }

    /*plan과 연결된 todoDateDaily를 모두 삭제*/
    @Test
    public void deleteDailyByPlan_Test() throws Exception {
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
        /*todoDate 저장*/
        TodoDateDaily todoDate = new TodoDateDaily(TodoStatus.UNCHECKED, start, "todoDate1", plan);
        todoDateService.save(todoDate);

        //when
        todoDateService.deleteDailyByPlan(LocalDate.now(), plan.getId());

        //then
        Assertions.assertThat(planRepository.findOne(plan.getId()).getUnchecked_TodoDate_cnt()).isEqualTo(0);

    }
}