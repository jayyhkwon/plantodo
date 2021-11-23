package demo.plantodo.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Embeddable
@Getter
public class Period {
    private LocalDate startDate;
    private LocalDate endDate;

    protected Period() {
    }

    public Period(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
