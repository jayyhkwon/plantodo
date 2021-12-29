package demo.plantodo.VO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
public class FilteredPlanVO {
    @DateTimeFormat(pattern = "yyyy-MM-dd") private LocalDate searchStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd") private LocalDate searchEnd;
    @DateTimeFormat(pattern = "yyyy-MM-dd") private LocalDate planStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd") private LocalDate planEnd;

    public FilteredPlanVO(LocalDate searchStart, LocalDate searchEnd, LocalDate planStart, LocalDate planEnd) {
        this.searchStart = searchStart;
        this.searchEnd = searchEnd;
        this.planStart = planStart;
        this.planEnd = planEnd;
    }
}
