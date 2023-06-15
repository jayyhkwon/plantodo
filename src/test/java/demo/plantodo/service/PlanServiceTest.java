package demo.plantodo.service;

import demo.plantodo.PlantodoApplication;
import demo.plantodo.VO.PlanListVO;
import demo.plantodo.domain.*;
import demo.plantodo.form.PlanTermRegisterForm;
import demo.plantodo.form.PlanTermUpdateForm;
import demo.plantodo.form.TodoRegisterForm;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(classes = {PlantodoApplication.class})
class PlanServiceTest {
    @Autowired private SettingsService settingsService;
    @Autowired private AuthService authService;
    @Autowired private PlanService planService;
    @Autowired private MemberService memberService;
    @Autowired private TodoDateService todoDateService;
    @Autowired private TodoService todoService;

    @Autowired private CommentService commentService;

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


    @Test
    @DisplayName("(endDate = Today), (isEmphasis = false)")
    public void findUrgentPlansWithEmphasis_Test_1() throws Exception {
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

        //then
        Assertions.assertThat(planService.findUrgentPlans(member.getId()).size()).isEqualTo(0);

    }

    @Test
    @DisplayName("(endDate = Today), (isEmphasis = true)")
    public void findUrgentPlansWithEmphasis_Test_2() throws Exception {
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

        Plan plan = planService.findAllPlan(member.getId()).get(0);
        plan.switchEmphasis();

        //then
        Assertions.assertThat(planService.findUrgentPlans(member.getId()).size()).isEqualTo(1);
    }

    @Test
    @DisplayName("term1 [(endDate = Today), (isEmphasis = true)], regular1")
    public void findUrgentPlansWithEmphasis_Test_3() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        /*PlanTerm 저장*/
        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String endTime = LocalTime.now().plusHours(1).format(formatter);
        PlanTermRegisterForm form = new PlanTermRegisterForm("planTerm", start, end, endTime);
        planService.saveTerm(member, form);

        /*planRegular 저장*/
        PlanRegular regular = new PlanRegular(member, PlanStatus.NOW, start, "planRegular");
        planService.saveRegular(regular);

        Plan plan = planService.findAllPlan(member.getId()).get(0);
        plan.switchEmphasis();

        //then
        Assertions.assertThat(planService.findUrgentPlans(member.getId()).size()).isEqualTo(1);
    }

    @Test
    public void planTermDeleteTest_AfterRevision230204() throws Exception {
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        /*PlanTerm 저장*/
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String endTime = LocalTime.now().plusHours(1).format(formatter);
        PlanTermRegisterForm form1 = new PlanTermRegisterForm("planTerm", start, end, endTime);
        planService.saveTerm(member, form1);

        /*plan 조회*/
        Plan plan = planService.findAllPlan(member.getId()).get(0);

        /*To-do (TodoDateRep 자동 생성)*/
        Todo todo = new Todo(member, plan, "test-to-do", 0, null);
        todoService.save(plan, todo);

        /*TodoDate(Daily)*/
        TodoDateDaily todoDate = new TodoDateDaily(TodoStatus.UNCHECKED, LocalDate.now(), "todoDate-test", plan);
        todoDateService.save(todoDate);

        /*Comment(Daily)*/
        commentService.save(todoDate.getId(), "test comment");

        /*planTerm 삭제*/
        planService.delete(plan.getId());

        /*plan, to-do, todoDate, comment 삭제되었는지 확인*/
        Assertions.assertThat(planService.findAllPlan(member.getId())).isEmpty();
        Assertions.assertThat(todoService.findAllTodoByMemberId(member.getId())).isEmpty();

        // plan으로 Daily 조회해서 삭제되었는지 확인
        Assertions.assertThat(todoDateService.getTodoDateDailyByPlanId(plan.getId())).isEmpty();

        // todo로 Rep 조회해서 삭제되었는지 확인
        Assertions.assertThat(todoDateService.getTodoDateByTodo(todo)).isEmpty();

        // todoDate로 Comment 조회해서 삭제되었는지 확인
        Assertions.assertThat(commentService.findAllCommentsByTodoDateId(todoDate.getId())).isEmpty();
    }

    @Test
    public void planRegularDeleteTest_AfterRevision230204() throws Exception {
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberService.save(member);

        /*planRegular 저장*/
        PlanRegular plan = new PlanRegular(member, PlanStatus.NOW, LocalDate.now(), "planRegular");
        planService.saveRegular(plan);

        /*To-do, Comment 저장*/
        Todo todo = new Todo(member, plan, "test-to-do", 0, null);
        todoService.save(plan, todo);

        List<TodoDate> todoDates = todoDateService.getTodoDateByTodo(todo);
        Assertions.assertThat(todoDates.size()).isEqualTo(1);

        TodoDate todoDate = todoDates.get(0);
        commentService.save(todoDate.getId(), "test comment");

        /*plan 삭제*/
        planService.delete(plan.getId());

        /*assert*/
        Assertions.assertThat(planService.findAllPlan(member.getId())).isEmpty();
        Assertions.assertThat(todoService.getTodoByPlanId(plan.getId())).isEmpty();
        Assertions.assertThat(todoDateService.getTodoDateByTodo(todo)).isEmpty();
        Assertions.assertThat(commentService.findAllCommentsByTodoDateId(todoDate.getId())).isEmpty();

    }

    @Test
    public void findAllPlan_withCompPercent_Test() throws Exception {
        //given

        Settings settings = new Settings(PermStatus.GRANTED);
        settingsService.save(settings);

        Member member = new Member("test@abc.co.kr", "abc123!@#", "test", settings);
        memberService.save(member);

        authService.save(member);

        PlanRegular plan = new PlanRegular(member, PlanStatus.NOW, LocalDate.now(), "plan1");
        planService.saveRegular(plan);

        PlanRegular plan2 = new PlanRegular(member, PlanStatus.COMPLETED, LocalDate.now(), "plan2");
        planService.saveRegular(plan2);

        PlanRegular plan3 = new PlanRegular(member, PlanStatus.PAST, LocalDate.now(), "plan3");
        planService.saveRegular(plan3);

        /*now 1*/
        PlanTerm plan4 = new PlanTerm(member, PlanStatus.NOW, LocalDate.now(), "plan4", LocalDate.now().plusDays(7), LocalTime.of(23, 59));
        planService.saveTerm(plan4);

        /*now 2*/
        PlanTerm plan5 = new PlanTerm(member, PlanStatus.NOW, LocalDate.now().plusDays(1), "plan5", LocalDate.now().plusDays(7), LocalTime.of(23, 59));
        planService.saveTerm(plan5);

        /*completed*/
        PlanTerm plan6 = new PlanTerm(member, PlanStatus.COMPLETED, LocalDate.now().plusDays(1), "plan6", LocalDate.now().plusDays(7), LocalTime.of(23, 59));
        planService.saveTerm(plan6);

        /*past*/
        PlanTerm plan7 = new PlanTerm(member, PlanStatus.PAST, LocalDate.now().minusDays(7), "plan7", LocalDate.now().minusDays(4), LocalTime.of(23, 59));
        planService.saveTerm(plan7);

        //when
        List<PlanListVO> results = planService.findAllPlan_withCompPercent(member.getId());
        results.forEach(planListVO -> {
            System.out.println(planListVO.getTitle());
        });
    }
}