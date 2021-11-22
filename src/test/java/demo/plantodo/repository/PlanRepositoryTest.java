package demo.plantodo.repository;

import demo.plantodo.domain.Member;
import demo.plantodo.domain.Period;
import demo.plantodo.domain.Plan;
import demo.plantodo.domain.PlanStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class PlanRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired PlanRepository planRepository;

    @Test
    public void planSaveTest() throws Exception {
        //given
        //1. Member 저장
        Member member = new Member("abc@naver.com", "12345678", "abc");
        memberRepository.save(member);
        // 2. Plan 생성
        Period period = makePeriod();
        Plan plan = new Plan(member, PlanStatus.NOW, period, "maked");

        //when
        planRepository.save(plan);

        //then
        Plan getPlan = planRepository.findPlanByTitle(plan.getTitle()).get(0);
        Assertions.assertThat(getPlan.getTitle().equals(plan.getTitle()));
    }

    private Period makePeriod() {
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = time1.plusDays(1);
        Period period = new Period(time1, time2);
        return period;
    }
    
    
    @Test
    public void findAllPlanTest() throws Exception {
        //given
        // 1. Member 저장 (1명)
        Member member = new Member("abc@naver.com", "12345678", "abc");
        memberRepository.save(member);
        // 2. plan 저장 (2개)
        Period period = makePeriod();
        Plan plan1 = new Plan(member, PlanStatus.NOW, period, "병원방문");
        Plan plan2 = new Plan(member, PlanStatus.NOW, period, "장보기");

        //when
        List<Plan> allPlan = planRepository.findAllPlan(member.getId());

        //then
        Assertions.assertThat(allPlan.size() == 2);
    }
}