package com.governai.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface AppUserRepository extends JpaRepository<AppUser, UUID> { List<AppUser> findByOrganization_Id(UUID organizationId); boolean existsByEmailIgnoreCase(String email); }
