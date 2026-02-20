package vn.id.luannv.auth_service.config.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.id.luannv.auth_service.entity.Role;
import vn.id.luannv.auth_service.entity.User;
import vn.id.luannv.auth_service.entity.UserRole;
import vn.id.luannv.auth_service.entity.UserStatus;
import vn.id.luannv.auth_service.repository.RoleRepository;
import vn.id.luannv.auth_service.repository.UserRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${bootstrap.admin.username:}")
    private String adminUsername;

    @Value("${bootstrap.admin.password:}")
    private String adminPassword;

    @Value("${bootstrap.admin.email:}")
    private String adminEmail;

    @Override
    public void run(String... args) {

        for (UserRole userRole : UserRole.values()) {
            String roleName = "ROLE_" + userRole.name();
            if (!roleRepository.existsByName(roleName)) {
                roleRepository.save(
                        Role.builder()
                                .name(roleName)
                                .description(userRole.name() + " role")
                                .build()
                );
            }
        }

        if (!userRepository.existsByUsername(adminUsername)) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow();

            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .email(adminEmail)
                    .fullName("System Administrator")
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(admin);
        }
    }
}
