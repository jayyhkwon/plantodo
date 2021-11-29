package demo.plantodo.form;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Locale;

@Getter
public class CalendarSearchForm {
    int targetYear;
    int targetMonth;

    public CalendarSearchForm() {
    }

    public CalendarSearchForm(int targetYear, int targetMonth) {
        this.targetYear = targetYear;
        this.targetMonth = targetMonth;
    }

    public LocalDate[][] makeCalendar(int yearValue, int monthValue, int length) {

        int rowNum = 0;

        LocalDate[][] calendar = new LocalDate[6][7];
        for (int i = 1; i < length + 1; i++) {
            LocalDate day = LocalDate.of(yearValue, monthValue, i);
            DayOfWeek dayOfWeek = day.getDayOfWeek();
            int colNum = DayOfWeekToNum(dayOfWeek);
            calendar[rowNum][colNum] = day;
            if (colNum == 6) {
                rowNum += 1;
            }
        }
        return calendar;
    }

    private int DayOfWeekToNum(DayOfWeek dayOfWeek) {
        int colNum;
        switch (dayOfWeek) {
            case SUNDAY:
                colNum = 0;
                break;
            case MONDAY:
                colNum = 1;
                break;
            case TUESDAY:
                colNum = 2;
                break;
            case WEDNESDAY:
                colNum = 3;
                break;
            case THURSDAY:
                colNum = 4;
                break;
            case FRIDAY:
                colNum = 5;
                break;
            case SATURDAY:
                colNum = 6;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dayOfWeek);
        }
        return colNum;
    }
}
