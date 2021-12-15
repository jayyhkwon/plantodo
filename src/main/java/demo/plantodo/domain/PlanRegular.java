package demo.plantodo.domain;

import lombok.*;

import javax.persistence.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@DiscriminatorValue("Regular")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanRegular extends Plan {

    @Builder
    public PlanRegular(Member member, PlanStatus planStatus, LocalDate startDate, String title) {
        super(member, planStatus, startDate, title);
    }
}
