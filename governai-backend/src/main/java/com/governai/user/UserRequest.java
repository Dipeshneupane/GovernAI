package com.governai.user;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
public record UserRequest(UUID organizationId, @NotBlank @Email @Size(max = 255) String email, @Size(max = 100) String firstName, @Size(max = 100) String lastName, Boolean active, Set<@NotBlank String> roles) { }
