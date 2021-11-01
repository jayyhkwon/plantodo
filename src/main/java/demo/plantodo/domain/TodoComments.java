package demo.plantodo.domain;

import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class TodoComments {
    private List<String> comments = new ArrayList<>();
}
