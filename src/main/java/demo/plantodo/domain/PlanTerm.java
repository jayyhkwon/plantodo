package demo.plantodo.domain;

import lombok.*;

import javax.persistence.*;

import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@DiscriminatorValue("Term")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanTerm extends Plan{

    private LocalDate endDate;

    @Builder
    public PlanTerm(Member member, PlanStatus planStatus, LocalDate startDate, String title, LocalDate endDate) {
        super(member, planStatus, startDate, title);
        this.endDate = endDate;
    }
}
