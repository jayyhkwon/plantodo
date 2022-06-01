package demo.plantodo.service;

import demo.plantodo.domain.*;
import demo.plantodo.repository.PlanRepository;
import demo.plantodo.repository.TodoDateRepository;
import demo.plantodo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoDateService {

    private final PlanRepository planRepository;
    private final TodoDateRepository todoDateRepository;
    private final TodoRepository todoRepository;
    private final CommonService commonService;

    /*등록*/
    public void save(TodoDate todoDate) {
        planRepository.addUnchecked(getPlanId(todoDate), 1);
        todoDateRepository.save(todoDate);
    }

    /*조회*/
    public TodoDate findOne(Long todoDateId) {
        return todoDateRepository.findOne(todoDateId);
    }

    public TodoDate findOneRep(Long todoDateId) {
        return todoDateRepository.findOneRep(todoDateId);
    }

    public TodoDate findOneDaily(Long todoDateId) {
        return todoDateRepository.findOneDaily(todoDateId);
    }

    public LinkedHashMap<LocalDate, List<TodoDate>> allTodoDatesInTerm(Plan plan, @Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        if (startDate==null && endDate==null) {
            startDate = plan.getStartDate();
            endDate = LocalDate.now();
            if (plan.getDtype().equals("Term")) {
                PlanTerm planTerm = (PlanTerm) plan;
                endDate = planTerm.getEndDate();
            }
        }
        int days = Period.between(startDate, endDate).getDays();

        LinkedHashMap<LocalDate, List<TodoDate>> allTodosByDate = new LinkedHashMap();
        /*startDate에는 getTodoDateAndPlan을 적용하지 않고 그냥 todoDate를 조회만 하기*/
        /*startDate 다음 날부터는 getTodoDateAndPlan을 적용하기*/

        for (int i = 0; i < days + 1; i++) {
            LocalDate date = startDate.plusDays(i);
            List<TodoDate> todoDateList = new ArrayList<>();
            if (date.isEqual(LocalDate.now())) {
                todoDateList = getTodoDateByDateAndPlan(plan, date, false);
            } else {
                todoDateList = getTodoDateByDateAndPlan(plan, date, true);
            }

            if (!todoDateList.isEmpty()) {
                allTodosByDate.put(date, todoDateList);
            }
        }
        return allTodosByDate;
    }


    public List<TodoDate> getTodoDateByDateAndPlan(Plan plan, LocalDate searchDate, boolean needUpdate) {
        /*searchDate 검증*/
        if (plan instanceof PlanTerm) {
            PlanTerm planTerm = (PlanTerm) plan;
            if (searchDate.isBefore(planTerm.getStartDate()) || searchDate.isAfter(planTerm.getEndDate())) {
                return new ArrayList<>();
            }
        }
        if (plan instanceof PlanRegular) {
            if (searchDate.isBefore(plan.getStartDate())) {
                return new ArrayList<>();
            }
        }
        List<Todo> todolist = todoRepository.getTodoByPlanIdAndDate(plan, searchDate);
        if (todolist.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<TodoDate> todoDateList = new ArrayList<>();

        for (Todo todo : todolist) {
            /*해당 날짜에 todo로 todoDate를 만들 수 있는지?*/
            if (canMakeTodoDate(todo, searchDate)) {
                List<TodoDate> result = todoDateRepository.getTodoDateByTodoAndDate(todo, searchDate);
                /*planTerm인 경우 무조건 todoDate가 있음*/
                /*planRegular이고 이미 해당 날짜에 생성되어 있는 todoDate가 있음*/
                if (plan instanceof PlanTerm || !result.isEmpty()) {
                    for (TodoDate todoDate : result) {
                        todoDateList.add(todoDate);
                    }
                } else {
                    if (needUpdate) {
                        /*PlanRegular이면서 해당 날짜에 todoDate를 만들 수 있는데 없는 경우 새로 만들어서 저장*/
                        TodoDateRep todoDateRep = new TodoDateRep(TodoStatus.UNCHECKED, searchDate, todo);
                        save(todoDateRep);
                        todoDateList.add(todoDateRep);
                    }
                }
            }
        }

        List<TodoDate> notBindingTodo = todoDateRepository.getTodoDateByPlanAndDate(plan, searchDate);
        for (TodoDate todoDate : notBindingTodo) {
            todoDateList.add(todoDate);
        }
        return todoDateList;
    }

    public List<TodoDate> getTodoDateRep_ByTodoAndDate(Todo todo, LocalDate searchDate) {
        return todoDateRepository.getTodoDateRep_ByTodoAndDate(todo, searchDate);
    }

    public boolean canMakeTodoDate(Todo todo, LocalDate date) {
        int repOption = todo.getRepOption();
        switch (repOption) {
            case 1:
                String dayOfWeek = commonService.turnDayOfWeekToString(date.getDayOfWeek());
                List<String> repValue_case1 = todo.getRepValue();
                if (repValue_case1.contains(dayOfWeek)) {
                    return true;
                }
                return false;
            case 2:
                int repValue_case2 = Integer.parseInt(todo.getRepValue().get(0));
                LocalDate startDate = todo.getPlan().getStartDate();
                int diffDays = Period.between(startDate, date).getDays();
                if ((diffDays % repValue_case2) == 0) {
                    return true;
                }
                return false;
            default:
                return true;
        }
    }


    public int todoDateInitiate(LocalDate startDate, LocalDate endDate, Todo todo) {
        int days = Period.between(startDate, endDate).getDays();
        int cnt = 0;
        for (int i = 0; i < days+1; i++) {
            LocalDate date = startDate.plusDays(i);
            if (canMakeTodoDate(todo, date)) {
                cnt ++;
                TodoDateRep todoDateRep = new TodoDateRep(TodoStatus.UNCHECKED, date, todo);
                save(todoDateRep);
            }
        }
        return cnt;
    }

    /*상태 변경 메서드*/
    public void switchStatusRep(Long todoDateId) {
        TodoDateRep todoDateRep = todoDateRepository.switchStatusRep(todoDateId);
        /* checked -> unchecked (plan에 반영) */
        checkStatAndReviseCnt(todoDateRep);
    }

    public void switchStatusDaily(Long todoDateId) {
        TodoDateDaily todoDateDaily = todoDateRepository.switchStatusDaily(todoDateId);
        /* unchecked -> checked (plan에 반영) */
        checkStatAndReviseCnt(todoDateDaily);
    }

    private void checkStatAndReviseCnt(TodoDate todoDate) {

        Long planId = getPlanId(todoDate);

        if (todoDate.getTodoStatus().equals(TodoStatus.CHECKED)) {
            planRepository.exchangeCheckedToUnchecked(planId, 1, -1);
        }
        if (todoDate.getTodoStatus().equals(TodoStatus.UNCHECKED)) {
            planRepository.exchangeCheckedToUnchecked(planId, -1, 1);
        }
    }

    private Long getPlanId(TodoDate todoDate) {
        return todoDate instanceof TodoDateRep ? ((TodoDateRep) todoDate).getTodo().getPlan().getId() : ((TodoDateDaily) todoDate).getPlan().getId();
    }

    /*삭제 메서드*/
    public void deleteRep(Long todoDateId) {
        TodoDateRep todoDateRep = todoDateRepository.findOneRep(todoDateId);
        Long planId = todoDateRep.getTodo().getPlan().getId();
        int[] counts = countCounts(todoDateRep);
        planRepository.deleteCheckedAndUnchecked(planId, counts[0], counts[1]);
        todoDateRepository.deleteRep(todoDateRep);
    }

    public void deleteDaily(Long todoDateId) {
        TodoDateDaily todoDateDaily = todoDateRepository.findOneDaily(todoDateId);
        Long planId = todoDateDaily.getPlan().getId();
        int[] counts = countCounts(todoDateDaily);
        planRepository.deleteCheckedAndUnchecked(planId, counts[0], counts[1]);
        todoDateRepository.deleteDaily(todoDateDaily);
    }

    private int[] countCounts(TodoDate todoDate) {
        int checked = 0;
        int unchecked = 0;

        if (todoDate.getTodoStatus().equals(TodoStatus.CHECKED)) {
            checked += 1;
        } else {
            unchecked += 1;
        }

        int[] res = {checked, unchecked};

        return res;
    }

    public int deleteDailyByPlan(LocalDate today, Long planId) {
        List<TodoDate> todoDateDailyList = todoRepository.findDailiesByPlanIdandDate(planId);
        int pastTodoDateCnt = 0;
        for (TodoDate todoDate : todoDateDailyList) {
            if (todoDate.getDateKey().isBefore(today)) {
                pastTodoDateCnt += 1;
            } else {
                deleteDaily(todoDate.getId());
            }
        }
        return pastTodoDateCnt;
    }

    public void updateTitle(Long todoDateId, String updateTitle) {
        todoDateRepository.updateTitle(todoDateId, updateTitle);
    }

    public List<TodoDate> getTodoDateByTodo(Todo todo) {
        return todoDateRepository.getTodoDateByTodo(todo);
    }
}
