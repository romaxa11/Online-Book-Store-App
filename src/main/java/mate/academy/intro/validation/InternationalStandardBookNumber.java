package mate.academy.intro.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = InternationalStandardBookNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InternationalStandardBookNumber {
    String message() default "Invalid format International Standard Book Number (ISBN)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
