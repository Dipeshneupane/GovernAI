package com.governai.user;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
public record UserResponse(UUID id, UUID organizationId, String email, String firstName, String lastName, boolean active, Set<String> roles, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    static UserResponse from(AppUser user) { return new UserResponse(user.getId(), user.getOrganization() == null ? null : user.getOrganization().getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.isActive(), user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new)), user.getCreatedAt(), user.getUpdatedAt()); }
}
