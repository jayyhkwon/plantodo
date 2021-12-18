package demo.plantodo.service;

import demo.plantodo.domain.*;
import demo.plantodo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;

    public void todoSave(Todo todo) {
        todoRepository.save(todo);
    }

    public List<Todo> getTodoByDate(Plan plan, LocalDate date) {
        List<Todo> resultList = todoRepository.getTodoByPlanIdAndDate(plan, date);
        return getTodoByDate_Filter(resultList, date);
    }

    public List<Todo> getTodoByDate_Filter(List<Todo> resultList, LocalDate date) {
        if (resultList.isEmpty()) {
            return resultList;
        }
        List<Todo> filteredTodo = new ArrayList<>();
        for (Todo todo : resultList) {
            int repOption = todo.getRepOption();
            switch (repOption) {
                case 1:
                    List<String> repValue1 = todo.getRepValue();
                    String dayOfWeek = turnDayOfWeekToString(date.getDayOfWeek());

                    if (repValue1.contains(dayOfWeek)) {
                        filteredTodo.add(todo);
                    }
                    break;
                case 2:
                    int repValue2 = Integer.parseInt(todo.getRepValue().get(0));
                    LocalDate startDate = todo.getPlan().getStartDate();
                    int diffDays = Period.between(startDate, date).getDays();
                    if ((diffDays % repValue2) == 0) {
                        filteredTodo.add(todo);
                    }
                    break;
                default:
                    filteredTodo.add(todo);
            }
        }
        return filteredTodo;
    }

    public boolean canMakeTodoDate(Todo todo, LocalDate date) {
        int repOption = todo.getRepOption();
        switch (repOption) {
            case 1:
                String dayOfWeek = turnDayOfWeekToString(date.getDayOfWeek());
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

    private String turnDayOfWeekToString (DayOfWeek dayOfWeek){
        String result;
        switch (dayOfWeek) {
            case MONDAY:
                result = "월";
                break;
            case TUESDAY:
                result = "화";
                break;
            case WEDNESDAY:
                result = "수";
                break;
            case THURSDAY:
                result = "목";
                break;
            case FRIDAY:
                result = "금";
                break;
            case SATURDAY:
                result = "토";
                break;
            case SUNDAY:
                result = "일";
                break;
            default:
                result = "";
        }
        return result;
    }

//    public void swtichStatus (Long todoId){
//        todoRepository.switchStatus(todoId);
//    }

    public void todoDateInitiate(LocalDate startDate, LocalDate endDate, Todo todo) {
        int days = Period.between(startDate, endDate).getDays();
        for (int i = 0; i < days+1; i++) {
            LocalDate date = startDate.plusDays(i);
            if (canMakeTodoDate(todo, date)) {
                TodoDate todoDate = new TodoDate(todo, TodoStatus.UNCHECKED, date);
                todoRepository.saveTodoDate(todoDate);
            }

        }
    }

    public List<TodoDate> getTodoDateByDateAndPlan(Plan plan, LocalDate searchDate) {
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
                List<TodoDate> result = todoRepository.getTodoDateByTodoAndDate(todo, searchDate);
                /*planTerm인 경우 무조건 todoDate가 있음*/
                /*planRegular이고 이미 해당 날짜에 생성되어 있는 todoDate가 있음*/
                if (plan instanceof PlanTerm || !result.isEmpty()) {
                    for (TodoDate todoDate : result) {
                        todoDateList.add(todoDate);
                    }
                } else {
                    /*PlanRegular이면서 해당 날짜에 todoDate를 만들 수 있는데 없는 경우 새로 만들어서 저장*/
                    TodoDate todoDate = new TodoDate(todo, TodoStatus.UNCHECKED, searchDate);
                    todoRepository.saveTodoDate(todoDate);
                    todoDateList.add(todoDate);
                }
            }
        }
        return todoDateList;
    }

    public void switchStatus(Long todoDateId) {
        todoRepository.switchStatus(todoDateId);
    }
}