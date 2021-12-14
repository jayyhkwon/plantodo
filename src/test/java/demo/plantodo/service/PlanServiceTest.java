package demo.plantodo.service;

import demo.plantodo.domain.Member;
import demo.plantodo.domain.Plan;
import demo.plantodo.domain.PlanStatus;
import demo.plantodo.domain.PlanTerm;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
class PlanServiceTest {
    @Autowired private PlanService planService;
    @Autowired private PlanRepository planRepository;
    @Autowired private MemberRepository memberRepository;

    @Test
    /*오늘 날짜가 endDate 이후이면 True를 리턴해야 함*/
    public void checkPlanTermCompleted_test() throws Exception {
        //given
        Member member = new Member("test123@test.com", "12345678", "test");
        memberRepository.save(member);

        LocalDate time1 = LocalDate.of(2021, 12, 1);
        LocalDate time2 = LocalDate.of(2021, 12, 7);
        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, time1, "공부", time2);
        planRepository.saveTerm(planTerm);

        //when
        Long id = planTerm.getId();
        Plan findPlan = planRepository.findOne(id);
//        boolean result = planService.checkPlanTermCompleted(findPlan);

        //then
//        Assertions.assertThat(result == true);
    }
}