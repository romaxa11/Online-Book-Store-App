package mate.academy.intro.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import mate.academy.intro.dto.book.CreateBookRequestDto;

public class TextValidator implements ConstraintValidator<Text, CreateBookRequestDto> {
    private static final String PATTERN_OF_TEXT = "^[A-z0-9- .,!@?:#$%&]{1,200}$";

    @Override
    public boolean isValid(
            CreateBookRequestDto bookRequestDto,
            ConstraintValidatorContext context
    ) {
        String title = bookRequestDto.getTitle();
        String author = bookRequestDto.getAuthor();
        String description = bookRequestDto.getDescription();
        return title != null
                && Pattern.compile(PATTERN_OF_TEXT).matcher(title).matches()
                && author != null
                && Pattern.compile(PATTERN_OF_TEXT).matcher(author).matches()
                && description != null
                && Pattern.compile(PATTERN_OF_TEXT).matcher(description).matches();
    }
}
