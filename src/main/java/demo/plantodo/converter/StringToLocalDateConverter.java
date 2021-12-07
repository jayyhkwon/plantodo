package demo.plantodo.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringToLocalDateConverter implements Converter<String, LocalDate> {
    /*String을 받아서 LocalDate로 바꿔주는 Converter*/

    @Override
    public LocalDate convert(String source) {
        if (source == "상수") {
            return LocalDate.now();
        }
        return LocalDate.parse(source, DateTimeFormatter.ISO_DATE);
    }
}
