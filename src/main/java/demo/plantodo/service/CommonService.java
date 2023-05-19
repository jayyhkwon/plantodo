package demo.plantodo.service;

import demo.plantodo.domain.Settings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.DayOfWeek;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final AuthService authService;
    private final SettingsService settingsService;

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

    public void setDATCookie(String key, HttpServletResponse response) {
        Long memberId = authService.getMemberIdByKey(key);
        Settings settings = settingsService.findOneByMemberId(memberId);
        ResponseCookie dat;
        if (settings.isDeadline_alarm() && settings.getDeadline_alarm_term() != 0) {
            dat = makeCookie("deadline_alarm_term", String.valueOf(settings.getDeadline_alarm_term()), 1800);
        } else {
            dat = makeCookie("deadline_alarm_term", "-1", 1800);
        }
        response.setHeader("Set-Cookie", dat.toString());
    }

    public ResponseCookie makeCookie(String name, String value, int max_age) {
        return ResponseCookie.from(name, value)
                .maxAge(max_age)
                .path("/")
                // .sameSite("None")
                // .secure(true)
                .build();
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
