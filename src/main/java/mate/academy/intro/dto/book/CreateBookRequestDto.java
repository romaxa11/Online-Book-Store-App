package mate.academy.intro.dto.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import mate.academy.intro.validation.Cover;
import mate.academy.intro.validation.InternationalStandardBookNumber;
import mate.academy.intro.validation.Text;

@Data
@Text
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBookRequestDto {
    private String title;
    private String author;
    @InternationalStandardBookNumber
    private String isbn;
    @NotNull
    @Min(value = 0, message = "Price cannot be less than zero...")
    private BigDecimal price;
    private String description;
    @Cover
    private String coverImage;
    private Set<Long> categoriesIds = new HashSet<>();
}
