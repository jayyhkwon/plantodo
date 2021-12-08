package demo.plantodo.repository;

import demo.plantodo.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class PlanRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired PlanRepository planRepository;
    @Autowired TodoRepository todoRepository;

    @Test
    public void planSaveTest() throws Exception {
        //given
        //1. Member 저장
        Member member = new Member("abc@naver.com", "12345678", "abc");
        memberRepository.save(member);
        // 2. Plan 생성
        // Period period = makePeriod();
        // Plan plan = new Plan(member, PlanStatus.NOW, period, "maked");

        //when
        // planRepository.save(plan);

        //then
        // Plan getPlan = planRepository.findPlanByTitle(plan.getTitle()).get(0);
        // Assertions.assertThat(getPlan.getTitle().equals(plan.getTitle()));
    }

//    private Period makePeriod() {
//        LocalDateTime time1 = LocalDateTime.now();
//        LocalDateTime time2 = time1.plusDays(1);
//        Period period = new Period(time1, time2);
//        return period;
//    }
    
    
    @Test
    public void findAllPlanTest() throws Exception {
        //given
        // 1. Member 저장 (1명)
        Member member = new Member("abc@naver.com", "12345678", "abc");
        memberRepository.save(member);
        // 2. plan 저장 (2개)
        // Period period = makePeriod();
        // Plan plan1 = new Plan(member, PlanStatus.NOW, period, "병원방문");
        // Plan plan2 = new Plan(member, PlanStatus.NOW, period, "장보기");

        //when
        // List<PlanRegular> allPlan = planRepository.findAllPlan(member.getId());

        //then
        //  Assertions.assertThat(allPlan.size() == 2);
    }

//    @Test
//    public void planTodoTest() throws Exception {
//        //given
//        Member member = new Member("abc@naver.com", "12345678", "abc");
//        memberRepository.save(member);
//
//        String time1 = LocalDate.now().toString();
//        String time2 = LocalDate.now().plusDays(1).toString();
//        PlanRegular plan = new PlanRegular(member, PlanStatus.NOW, time1, time2, "공부");
//        planRepository.save(plan);
//
//        To1do todo1 = new To1do(member, plan, TodoStatus.UNCHECKED, "JPA 복습", 0, null);
//        To1do todo2 = new To1do(member, plan, TodoStatus.UNCHECKED, "MVC 복습", 0, null);
//        todoRepository.save(todo1);
//        todoRepository.save(todo2);
//
//        //when
//        List<To1do> todos = todoRepository.getTodoByPlanId(plan.getId());
//        for (To1do to1do : todos) {
//            System.out.println(to1do.getTitle());
//        }
//        //then
//        Assertions.assertThat(todos.size() == 2);
//
//    }
    
    @Test
    public void planRegularSave() throws Exception {
        //given
        Member member = new Member("abc@naver.com", "12345678", "abc");
        memberRepository.save(member);

        PlanRegular planRegular = new PlanRegular(member, PlanStatus.NOW, LocalDate.now(), "plan 수정");

        //when
        planRepository.saveRegular(planRegular);
        List<PlanRegular> allPlanRegular = planRepository.findAllPlanRegular(member.getId());

        //then
        Assertions.assertThat(allPlanRegular.size() == 1);

    }

    @Test
    public void planTermSave() throws Exception {
        //given
        Member member = new Member("abc@naver.com", "12345678", "abc");
        memberRepository.save(member);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);
        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, endDate, "플랜 수정");
        planRepository.saveTerm(planTerm);

        //when
        List<PlanTerm> allPlanTerm = planRepository.findAllPlanTerm(member.getId());

        //then
        Assertions.assertThat(allPlanTerm.size() == 1);
    }
}