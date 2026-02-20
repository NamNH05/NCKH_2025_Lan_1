package vn.id.luannv.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.luannv.auth_service.entity.Role;
import vn.id.luannv.auth_service.entity.User;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
