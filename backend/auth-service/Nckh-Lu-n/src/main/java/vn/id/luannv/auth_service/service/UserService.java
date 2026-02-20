package vn.id.luannv.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vn.id.luannv.auth_service.dto.request.RegisterRequest;
import vn.id.luannv.auth_service.dto.request.UpdateProfileRequest;
import vn.id.luannv.auth_service.dto.response.UserResponse;
import vn.id.luannv.auth_service.entity.Role;
import vn.id.luannv.auth_service.entity.User;
import vn.id.luannv.auth_service.entity.UserRole;
import vn.id.luannv.auth_service.entity.UserStatus;
import vn.id.luannv.auth_service.exception.BusinessException;
import vn.id.luannv.auth_service.mapper.UserMapper;
import vn.id.luannv.auth_service.repository.RoleRepository;
import vn.id.luannv.auth_service.repository.UserRepository;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RoleRepository roleRepository;

    // --- Users CRUD logic ---
    @Cacheable(value = "usersCache")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }
    @Cacheable(key = "#id", value = "user")
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));
        return UserMapper.toResponse(user);
    }
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "usersCache", allEntries = true)
    })
    public String deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        return "User disabled.";
    }


    @CacheEvict(value = "usersCache", allEntries = true)
    public UserResponse createUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        Role userRole = roleRepository.findByName("ROLE_" + UserRole.USER.name())
                .orElseThrow(() -> new BusinessException("Default role not found"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .roles(Set.of(userRole))
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    /**
     * Update user profile (fullName, email, phone)
     * Used by users to update their own profile
     */
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "usersCache", allEntries = true)
    })
    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if new email is already used by another user
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);
        return UserMapper.toResponse(updatedUser);
    }

}
