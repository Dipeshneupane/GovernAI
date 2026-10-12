package com.governai.user;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/roles")
public class RoleController { private final RoleRepository roles; public RoleController(RoleRepository roles) { this.roles = roles; } @GetMapping public List<RoleResponse> list() { return roles.findAll().stream().map(r -> new RoleResponse(r.getId(), r.getName(), r.getDescription())).toList(); } public record RoleResponse(java.util.UUID id, String name, String description) {} }
