package demo.plantodo.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;

public class ObjToJsonConverter implements Converter<Object, String> {
    @SneakyThrows
    @Override
    public String convert(Object source) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(source);
    }
}
