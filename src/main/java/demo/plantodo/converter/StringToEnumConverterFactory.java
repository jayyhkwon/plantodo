package demo.plantodo.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Controller;

@Controller
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverter(targetType);
    }

    private final class StringToEnumConverter<T extends Enum> implements Converter<String, Enum> {
        private Class<T> enumType;
        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }
        @Override
        public T convert(String source) {
            return (T) Enum.valueOf(this.enumType, source.toUpperCase());
        }
    }
}
