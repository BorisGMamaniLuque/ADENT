package com.adent.identity.api;

import com.adent.identity.domain.MembershipRole;
import com.adent.identity.domain.Role;
import com.adent.identity.domain.TenantMembership;
import com.adent.identity.domain.UserAccount;
import com.adent.identity.repository.MembershipRoleRepository;
import com.adent.identity.repository.RoleRepository;
import com.adent.identity.repository.TenantMembershipRepository;
import com.adent.identity.repository.UserRepository;
import com.adent.shared.exception.BusinessException;
import com.adent.shared.exception.NotFoundException;
import com.adent.shared.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole('OWNER','ADMIN')")
public class TenantUserController {

    private final CurrentUser current;
    private final UserRepository users;
    private final TenantMembershipRepository memberships;
    private final MembershipRoleRepository membershipRoles;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    public TenantUserController(
            CurrentUser current,
            UserRepository users,
            TenantMembershipRepository memberships,
            MembershipRoleRepository membershipRoles,
            RoleRepository roles,
            PasswordEncoder encoder
    ) {
        this.current = current;
        this.users = users;
        this.memberships = memberships;
        this.membershipRoles = membershipRoles;
        this.roles = roles;
        this.encoder = encoder;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<UserSummary> list() {
        UUID tenantId = current.tenantId();
        return memberships.findByTenantIdAndActiveTrue(tenantId)
                .stream()
                .map(membership -> {
                    UserAccount user = users.findById(membership.getUserId()).orElseThrow();
                    List<String> roleCodes = membershipRoles.findByMembershipId(membership.getId())
                            .stream()
                            .map(MembershipRole::getRoleId)
                            .map(roleId -> roles.findById(roleId)
                                    .map(Role::getCode)
                                    .orElse("UNKNOWN"))
                            .toList();

                    return new UserSummary(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getPhone(),
                            membership.getStaffId(),
                            roleCodes,
                            user.isActive()
                    );
                })
                .toList();
    }

    @PostMapping
    @Transactional
    public UserSummary create(@Valid @RequestBody UserCreateRequest request) {
        UUID tenantId = current.tenantId();
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String phone = normalizePhone(request.phone());

        if (users.existsByUsernameIgnoreCase(username)) {
            throw new BusinessException("El usuario ya existe");
        }
        if (users.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("El correo ya existe");
        }

        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(encoder.encode(request.password()));
        user = users.save(user);

        TenantMembership membership = new TenantMembership();
        membership.setTenantId(tenantId);
        membership.setUserId(user.getId());
        membership.setStaffId(request.staffId());
        membership.setDefaultMembership(true);
        membership = memberships.save(membership);

        List<String> assignedRoles = assign(membership, request.roles());

        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                membership.getStaffId(),
                assignedRoles,
                user.isActive()
        );
    }

    @PatchMapping("/{id}/active")
    @Transactional
    public UserSummary active(@PathVariable UUID id, @RequestParam boolean active) {
        UserAccount user = users.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        TenantMembership membership = memberships
                .findByTenantIdAndUserIdAndActiveTrue(current.tenantId(), id)
                .orElseThrow(() -> new NotFoundException("Usuario no pertenece al establecimiento"));

        user.setActive(active);
        user = users.save(user);

        List<String> roleCodes = membershipRoles.findByMembershipId(membership.getId())
                .stream()
                .map(MembershipRole::getRoleId)
                .map(roleId -> roles.findById(roleId)
                        .map(Role::getCode)
                        .orElse("UNKNOWN"))
                .toList();

        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                membership.getStaffId(),
                roleCodes,
                user.isActive()
        );
    }

    private List<String> assign(TenantMembership membership, List<String> codes) {
        List<String> assigned = new ArrayList<>();

        for (String code : codes) {
            Role role = roles.findByCode(code.toUpperCase(Locale.ROOT))
                    .orElseThrow(() -> new BusinessException("Rol no reconocido: " + code));

            MembershipRole membershipRole = new MembershipRole();
            membershipRole.setMembershipId(membership.getId());
            membershipRole.setRoleId(role.getId());
            membershipRoles.save(membershipRole);
            assigned.add(role.getCode());
        }

        return assigned;
    }

    private String normalizePhone(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String prefix = value.trim().startsWith("+") ? "+" : "";
        String digits = value.replaceAll("\\D", "");
        return digits.isBlank() ? null : prefix + digits;
    }

    public record UserCreateRequest(
            @NotBlank String username,
            @Email @NotBlank String email,
            @Size(max = 30) String phone,
            @NotBlank @Size(min = 8) String password,
            UUID staffId,
            @NotEmpty List<String> roles
    ) {}

    public record UserSummary(
            UUID id,
            String username,
            String email,
            String phone,
            UUID staffId,
            List<String> roles,
            boolean active
    ) {}
}
