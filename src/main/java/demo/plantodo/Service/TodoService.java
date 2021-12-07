package demo.plantodo.Service;

import demo.plantodo.converter.StringToLocalDateConverter;
import demo.plantodo.domain.Plan;
import demo.plantodo.domain.Todo;
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
    StringToLocalDateConverter stringToLocalDateConverter = new StringToLocalDateConverter();

    public void todoSave(Todo todo) {
        todoRepository.save(todo);
    }

    public List<Todo> getTodoByDateFilter(Plan plan, LocalDate date) {
        List<Todo> resultList = todoRepository.getTodoByPlanIdAndDate(plan, date);

        /*이 단계에서 resultList가 비었을 경우 예외처리 필요*/
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
                    LocalDate startDate = stringToLocalDateConverter.convert(todo.getPlan().getStartDate());
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

    private String turnDayOfWeekToString(DayOfWeek dayOfWeek) {
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
}
