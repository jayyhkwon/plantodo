package demo.plantodo.domain;

import lombok.*;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@DiscriminatorValue("Term")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanTerm extends Plan{

    private LocalDate endDate;
    private LocalTime endTime;

    @Builder
    public PlanTerm(Member member, PlanStatus planStatus, LocalDate startDate, String title, LocalDate endDate, LocalTime endTime) {
        super(member, planStatus, startDate, title);
        this.endDate = endDate;
        this.endTime = endTime;
    }

    /*테스트용 생성자*/
    @Builder
    public PlanTerm(Member member, PlanStatus planStatus, LocalDate startDate, String title, LocalDate endDate) {
        super(member, planStatus, startDate, title);
        this.endDate = endDate;
        this.endTime = LocalTime.of(23, 59);
    }
}
