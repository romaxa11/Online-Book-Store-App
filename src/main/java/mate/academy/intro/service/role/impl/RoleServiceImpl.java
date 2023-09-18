package mate.academy.intro.service.role.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.intro.model.Role;
import mate.academy.intro.model.Role.RoleName;
import mate.academy.intro.repository.role.RoleRepository;
import mate.academy.intro.service.role.RoleService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByRoleName(RoleName roleName) {
        return roleRepository.findRoleByRoleName(roleName).orElseThrow(() ->
                new RuntimeException("can't find role by roleName: " + roleName));
    }
}
