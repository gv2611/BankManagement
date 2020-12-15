package application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import application.entity.Role;


public interface RoleRepository extends JpaRepository<Role,Integer> {
    Role findByRole(String role);
}
