package mate.academy.intro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @Min(0)
    private BigDecimal price;
    private String description;
    @Cover
    private String coverImage;
}
