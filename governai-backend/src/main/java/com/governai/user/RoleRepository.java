package com.governai.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
public interface RoleRepository extends JpaRepository<Role, UUID> { List<Role> findByNameIn(Collection<String> names); }
