package mate.academy.intro.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mate.academy.intro.validation.FieldMatch;

@FieldMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Attention! Wrong password or repeatPassword!..."
)
@Data
public class UserRegistrationRequest {
    @Email
    @NotBlank
    @Size(min = 8, max = 50)
    private String email;
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
    private String repeatPassword;
    @NotBlank
    @Size(min = 1, max = 30)
    private String firstName;
    @NotBlank
    @Size(min = 1, max = 40)
    private String lastName;
    @NotBlank
    @Size(min = 5, max = 200)
    private String shippingAddress;

}
