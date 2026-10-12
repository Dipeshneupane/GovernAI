package com.governai.user;
import com.governai.exception.OrganizationNotFoundException;
import com.governai.exception.ResourceNotFoundException;
import com.governai.organization.Organization;
import com.governai.organization.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class UserService {
    private final AppUserRepository users; private final RoleRepository roles; private final OrganizationRepository organizations;
    public UserService(AppUserRepository users, RoleRepository roles, OrganizationRepository organizations) { this.users = users; this.roles = roles; this.organizations = organizations; }
    @Transactional public UserResponse create(UserRequest request) { if (users.existsByEmailIgnoreCase(request.email())) throw new IllegalArgumentException("A user with that email already exists"); AppUser user = new AppUser(); apply(user, request); return UserResponse.from(users.save(user)); }
    @Transactional(readOnly = true) public UserResponse get(UUID id) { return UserResponse.from(find(id)); }
    @Transactional(readOnly = true) public List<UserResponse> list(UUID organizationId) { List<AppUser> result = organizationId == null ? users.findAll() : users.findByOrganization_Id(organizationId); return result.stream().map(UserResponse::from).toList(); }
    @Transactional public UserResponse update(UUID id, UserRequest request) { AppUser user = find(id); if (!user.getEmail().equalsIgnoreCase(request.email()) && users.existsByEmailIgnoreCase(request.email())) throw new IllegalArgumentException("A user with that email already exists"); apply(user, request); return UserResponse.from(users.save(user)); }
    @Transactional public void delete(UUID id) { users.delete(find(id)); }
    private AppUser find(UUID id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id)); }
    private void apply(AppUser user, UserRequest request) { user.setEmail(request.email().trim().toLowerCase(Locale.ROOT)); user.setFirstName(request.firstName()); user.setLastName(request.lastName()); if (request.active() != null) user.setActive(request.active()); if (request.organizationId() == null) user.setOrganization(null); else { Organization org = organizations.findById(request.organizationId()).orElseThrow(() -> new OrganizationNotFoundException(request.organizationId())); user.setOrganization(org); } Set<String> requested = request.roles() == null ? Set.of() : request.roles(); List<Role> found = roles.findByNameIn(requested); if (found.size() != requested.size()) throw new IllegalArgumentException("One or more requested roles do not exist"); user.setRoles(new LinkedHashSet<>(found)); }
}
