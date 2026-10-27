package com.adent.shared.config;

import com.adent.identity.domain.*;
import com.adent.identity.repository.*;
import com.adent.platform.domain.*;
import com.adent.platform.repository.*;
import com.adent.publicsite.service.PublicSiteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class SeedDataConfig {
    @Bean
    @Order(1)
    @Transactional
    CommandLineRunner seed(
            UserRepository users,
            TenantRepository tenants,
            OrganizationRepository orgs,
            TenantMembershipRepository memberships,
            RoleRepository roles,
            MembershipRoleRepository membershipRoles,
            PublicSiteService publicSiteService,
            PasswordEncoder encoder,
            @Value("${adent.seed.enabled:true}") boolean enabled,
            @Value("${adent.seed.superadmin.username:superadmin}") String saUser,
            @Value("${adent.seed.superadmin.password:ChangeMe123!}") String saPass,
            @Value("${adent.seed.owner.username:owner}") String ownerUser,
            @Value("${adent.seed.owner.password:Owner123!}") String ownerPass,
            @Value("${adent.seed.demo-tenant:demo-dental}") String tenantSlug
    ) {
        return args -> {
            if (!enabled) return;

            if (!users.existsByUsernameIgnoreCase(saUser)) {
                UserAccount u = new UserAccount();
                u.setUsername(saUser);
                u.setEmail(saUser + "@adent.local");
                u.setPasswordHash(encoder.encode(saPass));
                u.setPlatformAdmin(true);
                users.save(u);
            }

            Tenant tenant = tenants.findBySlugIgnoreCase(tenantSlug).orElseGet(() -> {
                Tenant value = new Tenant();
                value.setSlug(tenantSlug);
                value.setName("Clínica Dental Demo");
                return tenants.save(value);
            });

            if (orgs.findByTenantId(tenant.getId()).isEmpty()) {
                Organization organization = new Organization();
                organization.setTenantId(tenant.getId());
                organization.setLegalName("Clínica Dental Demo S.A.C.");
                organization.setTradeName("Clínica Dental Demo");
                organization.setEstablishmentType("CLINIC");
                organization.setLogoText("CD");
                orgs.save(organization);
            }

            publicSiteService.ensureForTenant(tenant);

            UserAccount owner = users.findByUsernameIgnoreCase(ownerUser).orElseGet(() -> {
                UserAccount u = new UserAccount();
                u.setUsername(ownerUser);
                u.setEmail(ownerUser + "@adent.local");
                u.setPasswordHash(encoder.encode(ownerPass));
                return users.save(u);
            });

            TenantMembership membership = memberships.findByTenantIdAndUserIdAndActiveTrue(tenant.getId(), owner.getId()).orElseGet(() -> {
                TenantMembership value = new TenantMembership();
                value.setTenantId(tenant.getId());
                value.setUserId(owner.getId());
                value.setDefaultMembership(true);
                return memberships.save(value);
            });
            if (!membership.isDefaultMembership()) {
                membership.setDefaultMembership(true);
                memberships.save(membership);
            }

            for (String code : List.of("OWNER", "ADMIN", "RECEPTION", "DENTIST", "DENTAL_ASSISTANT", "LAB_TECHNICIAN", "XRAY_TECHNICIAN", "SECURITY", "CLEANING")) {
                Role role = roles.findByCode(code).orElseGet(() -> {
                    Role r = new Role();
                    r.setCode(code);
                    r.setName(code);
                    return roles.save(r);
                });
                if (code.equals("OWNER") && membershipRoles.findByMembershipId(membership.getId()).stream()
                        .noneMatch(mr -> mr.getRoleId().equals(role.getId()))) {
                    MembershipRole mr = new MembershipRole();
                    mr.setMembershipId(membership.getId());
                    mr.setRoleId(role.getId());
                    membershipRoles.save(mr);
                }
            }
        };
    }
}
