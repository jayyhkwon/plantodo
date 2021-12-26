package demo.plantodo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;

@Service
@RequiredArgsConstructor
public class CommonService {
    public String turnDayOfWeekToString (DayOfWeek dayOfWeek){
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
