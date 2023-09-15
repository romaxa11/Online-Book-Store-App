package mate.academy.intro.service.role;

import mate.academy.intro.model.Role;
import mate.academy.intro.model.Role.RoleName;

public interface RoleService {
    Role getRoleByRoleName(RoleName roleName);
}
