package vn.id.luannv.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.id.luannv.auth_service.dto.request.RoleRequest;
import vn.id.luannv.auth_service.dto.response.RoleResponse;
import vn.id.luannv.auth_service.entity.Role;
import vn.id.luannv.auth_service.exception.BusinessException;
import vn.id.luannv.auth_service.repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RoleResponse getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Role not found"));
        return toResponse(role);
    }

    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new BusinessException("Role already exists");
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return toResponse(roleRepository.save(role));
    }

    public RoleResponse update(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Role not found"));

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        return toResponse(roleRepository.save(role));
    }

    public String delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Role not found"));

        roleRepository.delete(role);
        return "Deleted successfully";
    }

    private RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
