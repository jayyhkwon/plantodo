package demo.plantodo.service;

import demo.plantodo.domain.*;
import demo.plantodo.form.PlanTermRegisterForm;
import demo.plantodo.form.PlanTermUpdateForm;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

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
        assertThat(planService.findOne(plan.getId()).calculate_plan_compPercent()).isEqualTo(50.0f);
    }

    @Test
    public void switchPlanEmphasis_Test() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        /*plan 저장*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTerm plan = new PlanTerm(member, PlanStatus.NOW, start, "plan1", end);
        planService.saveTerm(plan);

        //when
        planService.switchPlanEmphasis(plan.getId());

        //then
        assertThat(planService.findOne(plan.getId()).isEmphasis()).isTrue();
    }

    @Test
    public void saveTerm_withEndTime_Test() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTermRegisterForm form = new PlanTermRegisterForm("plan1", start, end, "16:00");

        //when
        /*plan 저장*/
        planService.saveTerm(member, form);

        //then
        Long memberId = member.getId();
        assertThat(planService.findAllPlanTerm(memberId).get(0).getEndTime().equals(LocalTime.of(16, 0)));
    }

    @Test
    public void saveTerm_withoutEndTime_Test() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTermRegisterForm form = new PlanTermRegisterForm("plan1", start, end, "");

        //when
        /*plan 저장*/
        planService.saveTerm(member, form);

        //then
        Long memberId = member.getId();
        assertThat(planService.findAllPlanTerm(memberId).get(0).getEndTime().equals(LocalTime.of(23, 59)));
    }

    @Test
    public void updateTerm_noEndTime_editEndTime() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        /*plan 저장 (endTime 미기재)*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTermRegisterForm registerform = new PlanTermRegisterForm("plan1", start, end, "");
        planService.saveTerm(member, registerform);

        //when
        Long memberId = member.getId();
        Plan savedPlan = planService.findAllPlan(memberId).get(0);

        PlanTermUpdateForm updateForm = new PlanTermUpdateForm("plan1", start, end, "18:00");
        planService.updateTerm(updateForm, savedPlan.getId());

        //then
        PlanTerm findPlan = (PlanTerm) planService.findOne(savedPlan.getId());
        assertThat(findPlan.getEndTime().equals("18:00"));
    }

    @Test
    public void updateTerm_withEndTime_editEndTime() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        /*plan 저장 (endTime 미기재)*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTermRegisterForm registerform = new PlanTermRegisterForm("plan1", start, end, "17:00");
        planService.saveTerm(member, registerform);

        //when
        Long memberId = member.getId();
        Plan savedPlan = planService.findAllPlan(memberId).get(0);

        PlanTermUpdateForm updateForm = new PlanTermUpdateForm("plan1", start, end, "18:00");
        planService.updateTerm(updateForm, savedPlan.getId());

        //then
        PlanTerm findPlan = (PlanTerm) planService.findOne(savedPlan.getId());
        assertThat(findPlan.getEndTime().equals("18:00"));
    }

    @Test
    @DisplayName("종료일이 이미 지난 PlanTerm 조회")
    public void findAllPlan_makePlanPast_1() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        /*PlanTerm1 저장_endDate가 오늘 이전*/
        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = start.plusDays(1);
        PlanTermRegisterForm form = new PlanTermRegisterForm("plan1", start, end, "");
        planService.saveTerm(member, form);

        //when
        Plan findPlan = planService.findAllPlan(member.getId()).get(0);

        //then
        assertThat(findPlan.getPlanStatus()).isEqualTo(PlanStatus.PAST);
    }

    @Test
    @DisplayName("종료일이 오늘이지만 종료 시간이 지난 PlanTerm 조회")
    public void findAllPlan_makePlanPast_2() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        /*PlanTerm1 저장*/
        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String endTime = LocalTime.now().minusHours(1).format(formatter);
        PlanTermRegisterForm form = new PlanTermRegisterForm("plan1", start, end, endTime);
        planService.saveTerm(member, form);

        //when
        Plan findPlan = planService.findAllPlan(member.getId()).get(0);

        //then
        assertThat(planService.findOne(findPlan.getId()).getPlanStatus()).isEqualTo(PlanStatus.PAST);
        assertThat(planService.findOne(findPlan.getId()).getPlanStatus()).isNotEqualTo(PlanStatus.NOW);


    }

    @Test
    @DisplayName("종료일이 오늘이고 종료 시간이 지나지 않은 PlanTerm 조회")
    public void findAllPlan_makePlanPast_3() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        /*PlanTerm 저장*/
        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String endTime = LocalTime.now().plusHours(1).format(formatter);
        PlanTermRegisterForm form = new PlanTermRegisterForm("plan1", start, end, endTime);
        planService.saveTerm(member, form);

        //when
        Plan findPlan = planService.findAllPlan(member.getId()).get(0);

        //then
        assertThat(planService.findOne(findPlan.getId()).getPlanStatus()).isEqualTo(PlanStatus.NOW);
        assertThat(planService.findOne(findPlan.getId()).getPlanStatus()).isNotEqualTo(PlanStatus.PAST);
    }


}