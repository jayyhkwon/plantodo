package demo.plantodo.domain;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class TodoOndate {
    private List<LocalDateTime> ondate = new ArrayList<>();
}
