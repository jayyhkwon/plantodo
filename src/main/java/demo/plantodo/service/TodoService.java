package demo.plantodo.service;

import demo.plantodo.domain.*;
import demo.plantodo.form.TodoUpdateForm;
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

    public Todo findOneTodo(Long todoId) {
        return todoRepository.findOne(todoId);
    }

    public TodoDate findOneTodoDate(Long todoDateId) {
        return todoRepository.findOneTodoDate(todoDateId);
    }

    public List<Todo> getTodoByPlanId(Long planId) {
        return todoRepository.getTodoByPlanId(planId);
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
                List<TodoDate> result = todoRepository.getTodoDateByTodoAndDate(todo, searchDate);
                /*planTerm인 경우 무조건 todoDate가 있음*/
                /*planRegular이고 이미 해당 날짜에 생성되어 있는 todoDate가 있음*/
                if (plan instanceof PlanTerm || !result.isEmpty()) {
                    for (TodoDate todoDate : result) {
                        todoDateList.add(todoDate);
                    }
                } else {
                    if (needUpdate) {
                        /*PlanRegular이면서 해당 날짜에 todoDate를 만들 수 있는데 없는 경우 새로 만들어서 저장*/
                        TodoDate todoDate = new TodoDate(todo, TodoStatus.UNCHECKED, searchDate);
                        todoRepository.saveTodoDate(todoDate);
                        todoDateList.add(todoDate);
                    }
                }
            }
        }
        return todoDateList;
    }

    public void switchStatus(Long todoDateId) {
        todoRepository.switchStatus(todoDateId);
    }

    public void deleteTodo(Long todoId) {
        /*todoDate 모두 불러오기*/
        LocalDate today = LocalDate.now();
        List<TodoDate> todoDateByTodoId = todoRepository.getTodoDateByTodoId(todoId);
        /*오늘 날짜 이후의 todoDate에 delete함수를 호출해서 삭제하기*/
        int deleteCnt = 0;
        for (TodoDate todoDate : todoDateByTodoId) {
            if (todoDate.getDateKey().equals(today) || todoDate.getDateKey().isAfter(today)) {
                todoRepository.deleteTodoDate(todoDate.getId());
                deleteCnt += 1;
            }
        }
        if (deleteCnt == todoDateByTodoId.size()) {
            todoRepository.deleteTodo(todoId);
        }
    }

    public void updateTodo(TodoUpdateForm todoUpdateForm, Long todoId, Plan plan) {
        /*to-do 찾아오기*/
        Todo todo = todoRepository.findOne(todoId);
        LocalDate today = LocalDate.now();
        /*todoUpdateForm와 to-do의 repOption과 repValue에 변화가 있었는지 확인*/
        if (todo.getRepOption() != todoUpdateForm.getRepOption() || !todo.getRepValue().equals(todoUpdateForm.getRepValue())) {
            /*오늘 이후의 todoDate 삭제*/
            List<TodoDate> todoDateAfterToday = todoRepository.getTodoDateByTodoIdAfterToday(todoId, today);
            for (TodoDate todoDate : todoDateAfterToday) {
                todoRepository.deleteTodoDate(todoDate.getId());
            }
            /*to-do 저장*/
            todoRepository.updateTodo(todoUpdateForm, todoId);
            /*todoDate 다시 만들기*/
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now();
            if (plan.getDtype().equals("Term")) {
                PlanTerm planTerm = (PlanTerm) plan;
                endDate = planTerm.getEndDate();
            }
            System.out.println("startDate = " + startDate);
            System.out.println("endDate = " + endDate);

            Todo newTodo = todoRepository.findOne(todoId);
            todoDateInitiate(startDate, endDate, newTodo);
        } else {
            /*repOption도 repValue도 바뀌지 않은 경우(타이틀만 변경)*/
            /*to-do 업데이트*/
            todoRepository.updateTodo(todoUpdateForm, todoId);
            Todo newTodo = todoRepository.findOne(todoId);
            /*todoDate 업데이트 (타이틀 변경)*/
            List<TodoDate> todoDates = todoRepository.getTodoDateByTodoId(todoId);
            for (TodoDate todoDate : todoDates) {
                todoRepository.updateTodoDate(newTodo, todoDate.getId());
            }
        }
    }

    public void deleteTodoDate(Long todoDateId) {
        todoRepository.deleteTodoDate(todoDateId);
    }


    public List<TodoDateComment> getCommentsByTodoDateId(Long todoDateId) {
        return todoRepository.getCommentsByTodoDateId(todoDateId);
    }

    public void saveComment(Long todoDateId, String comment) {
        TodoDate todoDate = todoRepository.findOneTodoDate(todoDateId);
        TodoDateComment todoDateComment = new TodoDateComment(todoDate, comment);
        todoRepository.saveComment(todoDateComment);
    }

    public void deleteComment(Long commentId) {
        todoRepository.deleteComment(commentId);
    }
}