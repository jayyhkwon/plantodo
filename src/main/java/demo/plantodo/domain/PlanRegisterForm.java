package demo.plantodo.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter @Setter
public class PlanRegisterForm {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String title;
}
