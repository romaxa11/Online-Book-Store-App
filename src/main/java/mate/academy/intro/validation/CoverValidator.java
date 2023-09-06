package mate.academy.intro.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CoverValidator implements ConstraintValidator<Cover, String> {
    private static final String PATTERN_OF_COVER_ADDRESS = "http://"
            + "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}"
            + "/[a-z0-9._%+-]+\\.[jpg]{3}$";
    @Override
    public boolean isValid(String cover, ConstraintValidatorContext context) {
        return cover != null && Pattern.compile(PATTERN_OF_COVER_ADDRESS).matcher(cover).matches();
    }
}
