package com.adent.identity.service;

import com.adent.identity.api.*;
import com.adent.identity.domain.*;
import com.adent.identity.repository.*;
import com.adent.identity.service.recovery.RecoveryDeliveryService;
import com.adent.platform.domain.Organization;
import com.adent.platform.domain.Tenant;
import com.adent.platform.domain.TenantStatus;
import com.adent.platform.repository.OrganizationRepository;
import com.adent.platform.repository.TenantRepository;
import com.adent.shared.exception.BusinessException;
import com.adent.shared.exception.NotFoundException;
import com.adent.staff.domain.Staff;
import com.adent.staff.repository.StaffRepository;
import com.adent.structure.domain.Branch;
import com.adent.structure.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class AuthService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository users;
    private final TenantRepository tenants;
    private final OrganizationRepository organizations;
    private final TenantMembershipRepository memberships;
    private final MembershipRoleRepository membershipRoles;
    private final RoleRepository roles;
    private final StaffRepository staffRepository;
    private final BranchRepository branchRepository;
    private final PasswordResetTokenRepository resetTokens;
    private final RecoveryDeliveryService recoveryDelivery;
    private final JwtService jwtService;
    private final boolean developmentShowRecoveryCode;
    private final int recoveryExpiresMinutes;

    public AuthService(
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserRepository users,
            TenantRepository tenants,
            OrganizationRepository organizations,
            TenantMembershipRepository memberships,
            MembershipRoleRepository membershipRoles,
            RoleRepository roles,
            StaffRepository staffRepository,
            BranchRepository branchRepository,
            PasswordResetTokenRepository resetTokens,
            RecoveryDeliveryService recoveryDelivery,
            JwtService jwtService,
            @Value("${adent.recovery.dev-show-code:true}") boolean developmentShowRecoveryCode,
            @Value("${adent.recovery.expires-minutes:15}") int recoveryExpiresMinutes
    ) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.users = users;
        this.tenants = tenants;
        this.organizations = organizations;
        this.memberships = memberships;
        this.membershipRoles = membershipRoles;
        this.roles = roles;
        this.staffRepository = staffRepository;
        this.branchRepository = branchRepository;
        this.resetTokens = resetTokens;
        this.recoveryDelivery = recoveryDelivery;
        this.jwtService = jwtService;
        this.developmentShowRecoveryCode = developmentShowRecoveryCode;
        this.recoveryExpiresMinutes = recoveryExpiresMinutes;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String username = request.username().trim();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.password()));
        UserAccount user = users.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return issueSession(user);
    }

    /**
     * Alta autocontenida del primer usuario de una clínica/consultorio.
     * Crea tenant + organización + responsable (staff) + usuario + membresía OWNER + sede principal.
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = normalizeEmail(request.email());
        String phone = normalizePhone(request.phone());
        String ruc = blankToNull(request.ruc());

        if (users.existsByUsernameIgnoreCase(username)) throw new BusinessException("El nombre de usuario ya está registrado");
        if (users.existsByEmailIgnoreCase(email)) throw new BusinessException("El correo electrónico ya está registrado");
        if (phone != null && users.existsByPhone(phone)) throw new BusinessException("El teléfono celular ya está registrado");
        if (ruc != null && organizations.existsByRuc(ruc)) throw new BusinessException("El RUC ya está registrado");

        String establishmentName = request.establishmentName().trim();
        Tenant tenant = new Tenant();
        tenant.setName(establishmentName);
        tenant.setSlug(generateTenantSlug(establishmentName));
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setActive(true);
        tenant = tenants.save(tenant);

        Organization organization = new Organization();
        organization.setTenantId(tenant.getId());
        organization.setTradeName(establishmentName);
        organization.setLegalName(Optional.ofNullable(blankToNull(request.legalName())).orElse(establishmentName));
        organization.setEstablishmentType(Optional.ofNullable(blankToNull(request.establishmentType())).orElse("CLINIC").toUpperCase(Locale.ROOT));
        organization.setRuc(ruc);
        organization.setAddress(blankToNull(request.address()));
        organization.setPhone(Optional.ofNullable(normalizePhone(request.establishmentPhone())).orElse(phone));
        organization.setEmail(Optional.ofNullable(normalizeEmailNullable(request.establishmentEmail())).orElse(email));
        organization.setLogoText(brandInitials(establishmentName));
        organization = organizations.save(organization);

        Staff ownerStaff = new Staff();
        ownerStaff.setTenantId(tenant.getId());
        ownerStaff.setFirstNames(request.ownerFirstNames().trim());
        ownerStaff.setLastNames(request.ownerLastNames().trim());
        ownerStaff.setDocumentType(blankToNull(request.ownerDocumentType()));
        ownerStaff.setDocumentNumber(blankToNull(request.ownerDocumentNumber()));
        ownerStaff.setSpecialty(blankToNull(request.ownerSpecialty()));
        ownerStaff.setPhone(phone);
        ownerStaff.setEmail(email);
        ownerStaff.setActive(true);
        ownerStaff = staffRepository.save(ownerStaff);

        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPlatformAdmin(false);
        user.setActive(true);
        user = users.save(user);

        TenantMembership membership = new TenantMembership();
        membership.setTenantId(tenant.getId());
        membership.setUserId(user.getId());
        membership.setStaffId(ownerStaff.getId());
        membership.setDefaultMembership(true);
        membership.setActive(true);
        membership = memberships.save(membership);

        Role ownerRole = roles.findByCode("OWNER").orElseGet(() -> {
            Role role = new Role();
            role.setCode("OWNER");
            role.setName("Propietario");
            return roles.save(role);
        });
        MembershipRole membershipRole = new MembershipRole();
        membershipRole.setMembershipId(membership.getId());
        membershipRole.setRoleId(ownerRole.getId());
        membershipRoles.save(membershipRole);

        // El onboarding deja una sede principal lista. Pisos y consultorios se completan desde Configuración.
        Branch mainBranch = new Branch();
        mainBranch.setTenantId(tenant.getId());
        mainBranch.setName("Sede Principal");
        mainBranch.setAddress(blankToNull(request.address()));
        mainBranch.setActive(true);
        branchRepository.save(mainBranch);

        return issueSession(user);
    }

    @Transactional
    public PasswordRecoveryResponse requestRecovery(PasswordRecoveryRequest request) {
        String channel = normalizeChannel(request.channel());
        String destination = normalizeDestination(channel, request.destination());
        Optional<UserAccount> optionalUser = findUserByDestination(channel, destination);

        // Respuesta deliberadamente genérica para no revelar si una cuenta existe.
        String genericMessage = "Si los datos están registrados, se generó un código de recuperación.";
        if (optionalUser.isEmpty()) {
            return new PasswordRecoveryResponse(genericMessage, maskDestination(channel, destination), recoveryExpiresMinutes, null);
        }

        UserAccount user = optionalUser.get();
        String code = "%06d".formatted(RANDOM.nextInt(1_000_000));
        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(user.getId());
        token.setChannel(channel);
        token.setDestinationMasked(maskDestination(channel, destination));
        token.setCodeHash(passwordEncoder.encode(code));
        token.setExpiresAt(OffsetDateTime.now().plusMinutes(recoveryExpiresMinutes));
        token.setAttempts(0);
        resetTokens.save(token);

        recoveryDelivery.deliver(channel, destination, code, recoveryExpiresMinutes);
        return new PasswordRecoveryResponse(
                genericMessage,
                token.getDestinationMasked(),
                recoveryExpiresMinutes,
                developmentShowRecoveryCode ? code : null
        );
    }

    @Transactional
    public Map<String, String> resetPassword(PasswordResetRequest request) {
        String channel = normalizeChannel(request.channel());
        String destination = normalizeDestination(channel, request.destination());
        UserAccount user = findUserByDestination(channel, destination)
                .orElseThrow(() -> new BusinessException("Código inválido o vencido"));

        PasswordResetToken token = resetTokens
                .findTopByUserIdAndChannelAndUsedAtIsNullOrderByCreatedAtDesc(user.getId(), channel)
                .orElseThrow(() -> new BusinessException("Código inválido o vencido"));

        OffsetDateTime now = OffsetDateTime.now();
        if (token.getExpiresAt().isBefore(now) || token.getAttempts() >= 5) {
            throw new BusinessException("Código inválido o vencido");
        }
        if (!passwordEncoder.matches(request.code(), token.getCodeHash())) {
            token.setAttempts(token.getAttempts() + 1);
            resetTokens.save(token);
            throw new BusinessException("Código inválido o vencido");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        users.save(user);
        token.setUsedAt(now);
        resetTokens.save(token);
        return Map.of("message", "Contraseña actualizada correctamente");
    }

    private LoginResponse issueSession(UserAccount user) {
        if (user.isPlatformAdmin()) {
            var token = jwtService.issue(user.getUsername(), null, null, null, List.of("SUPER_ADMIN"), true);
            return new LoginResponse(token.value(), "Bearer", token.expiresAt(), user.getUsername(), null, null,
                    List.of("SUPER_ADMIN"), true, "ADENT", "A", null);
        }

        List<TenantMembership> activeMemberships = memberships.findByUserIdAndActiveTrue(user.getId());
        if (activeMemberships.isEmpty()) throw new BusinessException("El usuario no está asociado a un establecimiento activo");

        TenantMembership membership = activeMemberships.stream().filter(TenantMembership::isDefaultMembership).findFirst()
                .orElseGet(() -> activeMemberships.size() == 1 ? activeMemberships.getFirst() : null);
        if (membership == null) {
            throw new BusinessException("La cuenta pertenece a varios establecimientos y no tiene uno predeterminado");
        }

        Tenant tenant = tenants.findById(membership.getTenantId())
                .orElseThrow(() -> new NotFoundException("Organización no encontrada"));
        if (!tenant.isActive() || tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new BusinessException("La organización no está activa");
        }

        List<String> roleCodes = membershipRoles.findByMembershipId(membership.getId()).stream()
                .map(MembershipRole::getRoleId)
                .map(id -> roles.findById(id).map(Role::getCode).orElse(null))
                .filter(Objects::nonNull).distinct().toList();
        if (roleCodes.isEmpty()) throw new BusinessException("La membresía no tiene roles asignados");

        Organization org = organizations.findByTenantId(tenant.getId()).orElse(null);
        String organizationName = org == null
                ? tenant.getName()
                : Optional.ofNullable(blankToNull(org.getTradeName())).orElse(org.getLegalName());
        String logoText = org == null ? brandInitials(tenant.getName())
                : Optional.ofNullable(blankToNull(org.getLogoText())).orElse(brandInitials(organizationName));
        String logoUrl = org == null ? null : blankToNull(org.getLogoUrl());

        var token = jwtService.issue(user.getUsername(), tenant.getId(), membership.getId(), tenant.getSlug(), roleCodes, false);
        return new LoginResponse(token.value(), "Bearer", token.expiresAt(), user.getUsername(), tenant.getId().toString(),
                tenant.getSlug(), roleCodes, false, organizationName, logoText, logoUrl);
    }

    private Optional<UserAccount> findUserByDestination(String channel, String destination) {
        return "EMAIL".equals(channel) ? users.findByEmailIgnoreCase(destination) : users.findByPhone(destination);
    }

    private String normalizeChannel(String raw) {
        String value = raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
        return switch (value) {
            case "EMAIL", "CORREO" -> "EMAIL";
            case "SMS", "PHONE", "CELULAR", "TELEFONO", "TELÉFONO" -> "SMS";
            default -> throw new BusinessException("Canal de recuperación no válido");
        };
    }

    private String normalizeDestination(String channel, String destination) {
        if ("EMAIL".equals(channel)) return normalizeEmail(destination);
        String phone = normalizePhone(destination);
        if (phone == null || phone.length() < 7) throw new BusinessException("Número de celular no válido");
        return phone;
    }

    private String normalizeEmail(String value) {
        if (value == null || value.isBlank()) throw new BusinessException("Correo electrónico obligatorio");
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeEmailNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePhone(String value) {
        if (value == null || value.isBlank()) return null;
        String trimmed = value.trim();
        String prefix = trimmed.startsWith("+") ? "+" : "";
        String digits = trimmed.replaceAll("\\D", "");
        return digits.isBlank() ? null : prefix + digits;
    }

    private String maskDestination(String channel, String destination) {
        if (destination == null || destination.isBlank()) return "***";
        if ("EMAIL".equals(channel)) {
            int at = destination.indexOf('@');
            if (at <= 1) return "***" + (at >= 0 ? destination.substring(at) : "");
            return destination.substring(0, 2) + "***" + destination.substring(at);
        }
        String digits = destination.replaceAll("\\D", "");
        return "***" + digits.substring(Math.max(0, digits.length() - 4));
    }

    private String generateTenantSlug(String name) {
        String base = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (base.isBlank()) base = "establecimiento";
        if (base.length() > 52) base = base.substring(0, 52).replaceAll("-$", "");
        for (int i = 0; i < 10; i++) {
            String suffix = String.format("%06x", RANDOM.nextInt(0x1000000));
            String candidate = base + "-" + suffix;
            if (tenants.findBySlugIgnoreCase(candidate).isEmpty()) return candidate;
        }
        return base + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String brandInitials(String name) {
        if (name == null || name.isBlank()) return "A";
        String[] words = name.trim().split("\\s+");
        StringBuilder out = new StringBuilder();
        for (String word : words) {
            if (!word.isBlank() && Character.isLetterOrDigit(word.charAt(0))) out.append(Character.toUpperCase(word.charAt(0)));
            if (out.length() == 2) break;
        }
        return out.isEmpty() ? "A" : out.toString();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
