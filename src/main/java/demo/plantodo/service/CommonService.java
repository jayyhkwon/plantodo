package demo.plantodo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.DayOfWeek;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final AuthService authService;

    public Long getMemberId(HttpServletRequest request) {
        String authKey = getCookieVal(request, "AUTH");
        return authService.getMemberIdByKey(authKey);
    }

    public String getCookieVal(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        String res = "";
        for (Cookie c : cookies) {
            if (c.getName().equals(name)) {
                res = c.getValue();
                break;
            }
        }
        return res;
    }

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
