package demo.plantodo.repository;

import demo.plantodo.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class TodoRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired PlanRepository planRepository;
    @Autowired TodoRepository todoRepository;

    @Test
    public void getTodoByPlanIdAndDateTest() throws Exception {
        //given
        /*member*/
        Member member = new Member("abc@naver.com", "12345678", "abc");
        memberRepository.save(member);

        /*plan*/
        LocalDate time1 = LocalDate.now();
        LocalDate time2 = time1.plusDays(3);
        Plan plan = new Plan(member, PlanStatus.NOW, time1, time2, "공부");
        planRepository.save(plan);

        /*to-do*/
        Todo todo1 = new Todo(member, plan, TodoStatus.UNCHECKED, "JPA 복습", 0, null);
        Todo todo2 = new Todo(member, plan, TodoStatus.UNCHECKED, "MVC 복습", 0, null);
        todoRepository.save(todo1);
        todoRepository.save(todo2);

        //when
        List<Todo> result = todoRepository.getTodoByPlanIdAndDate(plan.getId(), time1);

        //then
        assertThat(result.size() == 2);
    }

}