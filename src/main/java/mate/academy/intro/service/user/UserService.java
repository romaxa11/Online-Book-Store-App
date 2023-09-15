package mate.academy.intro.service.user;

import mate.academy.intro.dto.user.UserRegistrationRequest;
import mate.academy.intro.dto.user.UserResponseDto;
import mate.academy.intro.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequest request) throws RegistrationException;
}
