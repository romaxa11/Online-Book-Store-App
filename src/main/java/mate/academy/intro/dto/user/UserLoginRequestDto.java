package mate.academy.intro.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @Email
        @NotEmpty
        @Size(min = 8, max = 30)
        String email,
        @NotEmpty
        @Size(min = 8, max = 20)
        String password
) {
}
