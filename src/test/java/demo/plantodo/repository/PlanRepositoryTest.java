package demo.plantodo.repository;

import demo.plantodo.domain.*;
import org.apache.tomcat.jni.Local;
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
        PlanTerm planTerm = new PlanTerm(member, PlanStatus.NOW, startDate, "플랜 수정", endDate);
        planRepository.saveTerm(planTerm);

        //when
        List<PlanTerm> allPlanTerm = planRepository.findAllPlanTerm(member.getId());

        //then
        Assertions.assertThat(allPlanTerm.size() == 1);
    }

    @Test
    public void findAllPlanTest() throws Exception {
        /*만료되어야 하는 Plan 3개 등록 -> findAllPlan -> Plan 중 PlanStatus가 now인 것을 세기*/
        //given
        /*멤버 등록*/
        Member member = new Member("abc@naver.com", "12345678", "abc");
        memberRepository.save(member);

        /*Plan(기간 만료. 상태 NOW) 3개 저장*/
        LocalDate time1 = LocalDate.of(2021, 12, 1);
        LocalDate time2 = LocalDate.of(2021, 12, 3);
        PlanTerm test1 = new PlanTerm(member, PlanStatus.NOW, time1, "test1", time2);
        PlanTerm test2 = new PlanTerm(member, PlanStatus.NOW, time1, "test2", time2);
        PlanTerm test3 = new PlanTerm(member, PlanStatus.NOW, time1, "test3", time2);
        planRepository.saveTerm(test1);
        planRepository.saveTerm(test2);
        planRepository.saveTerm(test3);

        //when
        List<Plan> allPlan = planRepository.findAllPlan(member.getId());
        int resultCnt = 0;
        for (Plan plan : allPlan) {
            System.out.println("plan.getPlanStatus() = " + plan.getPlanStatus());
            if (plan.getPlanStatus() == PlanStatus.NOW) {
                resultCnt += 1;
            }
        }

        //then
        Assertions.assertThat(resultCnt == 0);
    }

}