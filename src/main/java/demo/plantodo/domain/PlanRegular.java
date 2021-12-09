package demo.plantodo.domain;

import demo.plantodo.converter.StringToLocalDateConverter;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@DiscriminatorValue("Regular")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanRegular extends Plan {

    @Builder
    public PlanRegular(Member member, PlanStatus planStatus, LocalDate startDate, String title) {
        super(member, planStatus, startDate, title);
    }
}
