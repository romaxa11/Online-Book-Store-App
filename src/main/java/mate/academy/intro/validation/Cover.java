package mate.academy.intro.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CoverValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cover {
    String message() default "Invalid format of cover email address";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
