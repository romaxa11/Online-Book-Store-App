package mate.academy.intro.mapper;

import mate.academy.intro.config.MapperConfig;
import mate.academy.intro.dto.user.UserRegistrationRequest;
import mate.academy.intro.dto.user.UserResponseDto;
import mate.academy.intro.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    User toModel(UserRegistrationRequest request);

    UserResponseDto toDto(User user);
}
