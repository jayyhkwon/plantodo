package demo.plantodo.validation;

import demo.plantodo.form.MemberJoinForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Component
public class MemberJoinlValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        MemberJoinForm member = (MemberJoinForm) target;

        /*이메일 형식 검증*/
        String email = member.getEmail();
        String pattern = "^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]+@[a-zA-Z0-9.-]+$";
        if (!Pattern.matches(pattern, email)) {
            errors.rejectValue("email", "formal");
        }

        /*패스워드 길이 검증*/
        String password = member.getPassword();
        if (password.length() < 8 || password.length() > 24) {
            errors.rejectValue("password", "range", new Object[]{8, 24}, null);
        }

        /*패스워드 형식 검증*/
        int[] dat = new int[200];
        byte[] ascii = password.getBytes(StandardCharsets.UTF_8);
        for (byte b : ascii) {
            dat[b] = 1;
        }
        int[] asterisk = IntStream.range(32, 47).map(i -> dat[i]).toArray();
        int astSum = Arrays.stream(asterisk).parallel().reduce(0, (a, b) -> a + b);
        int[] numbers = IntStream.range(48, 57).map(i -> dat[i]).toArray();
        int nSum = Arrays.stream(numbers).parallel().reduce(0, (a, b) -> a + b);
        int[] upperAl = IntStream.range(65, 90).map(i -> dat[i]).toArray();
        int uaCnt = Arrays.stream(upperAl).parallel().reduce(0, (a, b) -> a + b);
        int[] lowerAl = IntStream.range(97, 122).map(i -> dat[i]).toArray();
        int laCnt = Arrays.stream(lowerAl).parallel().reduce(0, (a, b) -> a + b);
        if (astSum == 0 || nSum == 0 || uaCnt+laCnt == 0) {
            errors.rejectValue("password", "formal");
        }
    }
}
