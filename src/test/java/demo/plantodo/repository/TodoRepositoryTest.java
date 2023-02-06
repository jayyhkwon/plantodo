package demo.plantodo.repository;

import demo.plantodo.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class TodoRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired PlanRepository planRepository;
    @Autowired TodoRepository todoRepository;

/*    @Test
    public void getTodoByPlanIdAndDateTest1() throws Exception {
        *//*날짜에 해당하는 todo가 있을 경우 todo가 담긴 리스트를 반환한다.*//*
        //given
        *//*member*//*
        Member member = new Member("abc@naver.com", "12345678", "abc");
        memberRepository.save(member);

        *//*plan*//*
        LocalDate time1 = LocalDate.now();
        LocalDate time2 = time1.plusDays(3);
        Plan plan = new Plan(member, PlanStatus.NOW, time1, time2, "공부");
        planRepository.save(plan);

        *//*to-do*//*
        To1do todo1 = new To1do(member, plan, TodoStatus.UNCHECKED, "JPA 복습", 0, null);
        To1do todo2 = new To1do(member, plan, TodoStatus.UNCHECKED, "MVC 복습", 0, null);
        todoRepository.save(todo1);
        todoRepository.save(todo2);

        //when
        List<To1do> result = todoRepository.getTodoByPlanIdAndDate(plan, time1);

        //then
        assertThat(result.size() == 2);
    }*/

    @Test
    public void getTodoByPlanIdAndDateTest2() throws Exception {
/*        *//*날짜에 해당하는 todo가 없을 경우 빈 리스트를 반환한다.*//*
        //given
        Member member = new Member("abc@naver.com", "12345678", "abc");
        memberRepository.save(member);

        *//*plan*//*
        LocalDate time1 = LocalDate.now();
        LocalDate time2 = time1.plusDays(7);
        Plan plan = new Plan(member, PlanStatus.NOW, time1, time2, "공부");
        planRepository.save(plan);

        *//*to-do*//*
        *//*repOption 1*//*
        ArrayList<String> repValue1 = new ArrayList<>();
        repValue1.add("월");
        repValue1.add("수");
        To1do todo1 = new To1do(member, plan, TodoStatus.UNCHECKED, "JPA 복습", 1, repValue1);
        todoRepository.save(todo1);

        *//*repOption 2*//*
        ArrayList<String> repValue2 = new ArrayList<>();
        repValue2.add("2");
        To1do todo2 = new To1do(member, plan, TodoStatus.UNCHECKED, "MVC 복습", 2, repValue2);
        todoRepository.save(todo2);

        //when

        List<To1do> test1 = todoRepository.getTodoByPlanIdAndDate(plan, LocalDate.of(2021, 12, 2));
        List<To1do> test2 = todoRepository.getTodoByPlanIdAndDate(plan, LocalDate.of(2021, 12, 2));

        //then
        for (To1do to1do : test1) {
            System.out.println("test1.getTitle() = " + to1do.getTitle());
        }

        for (To1do to1do : test2) {
            System.out.println("test2.getTitle() = " + to1do.getTitle());
        }
        *//*12월 2일은 목요일 - test1의 결과는 빈 리스트*//*
        assertThat(test1.isEmpty());
        *//*12월 2일은 11월 30일 + 2 -> test2의 결과 리스트는 size 1*//*
        assertThat(test2.size() == 1);*/
    }

}