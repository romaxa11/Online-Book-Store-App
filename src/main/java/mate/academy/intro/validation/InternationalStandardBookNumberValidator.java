package mate.academy.intro.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class InternationalStandardBookNumberValidator implements
        ConstraintValidator<InternationalStandardBookNumber,String> {
    private static final String PATTERN_OF_ISBN =
            "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})"
            + "[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})"
            + "[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$";

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        return isbn != null && Pattern.compile(PATTERN_OF_ISBN).matcher(isbn).matches();
    }
}
