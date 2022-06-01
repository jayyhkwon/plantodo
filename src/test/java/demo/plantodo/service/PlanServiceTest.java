package demo.plantodo.service;

import demo.plantodo.domain.*;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
class PlanServiceTest {
    @Autowired private PlanService planService;
    @Autowired private MemberService memberService;
    @Autowired private TodoDateService todoDateService;
    @Autowired private TodoService todoService;

    @Test
    /*오늘 날짜가 endDate 이후이면 True를 리턴해야 함*/
    public void checkPlanTermCompleted_test() throws Exception {
//        //given
//        Member member = new Member("test123@test.com", "12345678", "test");
//        memberRepository.save(member);
//
//        LocalDate time1 = LocalDate.of(2021, 12, 1);
//        LocalDate time2 = LocalDate.of(2021, 12, 7);
//        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, time1, "공부", time2);
//        planRepository.saveTerm(planTerm);
//
//        //when
//        Long id = planTerm.getId();
//        Plan findPlan = planRepository.findOne(id);
////        boolean result = planService.checkPlanTermCompleted(findPlan);
//
//        //then
////        Assertions.assertThat(result == true);
    }


    @Test
    @DisplayName("달성도 로직 테스트")
    public void findAllPlan_withCompletionPercent_Test() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        /*plan 저장*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTerm plan = new PlanTerm(member, PlanStatus.NOW, start, "plan1", end);
        planService.saveTerm(plan);

        /*To-do 저장*/
        Todo todo = new Todo(member, plan, "todo1", 0, null);
        todoService.save(plan, todo);

        //when
        // (네 개 중 절반은 check, 절반은 uncheck)
        List<TodoDate> todoDates = todoDateService.getTodoDateByTodo(todo);
        int count = 0;
        for (TodoDate todoDate : todoDates) {
            if (count % 2 == 0) {
                todoDateService.switchStatusRep(todoDate.getId());
            }
            count ++;
        }

        // Then
        Assertions.assertThat(planService.findOne(plan.getId()).calculate_plan_compPercent()).isEqualTo(50.0f);
    }
}