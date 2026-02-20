package vn.id.luannv.auth_service.config.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vn.id.luannv.auth_service.entity.Role;
import vn.id.luannv.auth_service.entity.UserRole;
import vn.id.luannv.auth_service.repository.RoleRepository;

//@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        for (UserRole userRole : UserRole.values()) {
            String roleName = "ROLE_" + userRole.name();

            if (!roleRepository.existsByName(roleName)) {
                Role role = Role.builder()
                        .name(roleName)
                        .description(userRole.name() + " role")
                        .build();

                roleRepository.save(role);
            }
        }
    }
}
