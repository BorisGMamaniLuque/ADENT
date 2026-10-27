package com.adent.platform.api;

import com.adent.platform.domain.*;
import com.adent.platform.repository.*;
import com.adent.publicsite.domain.PublicSite;
import com.adent.publicsite.repository.PublicSiteRepository;
import com.adent.publicsite.service.PublicSiteService;
import com.adent.shared.exception.BusinessException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/platform/tenants")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PlatformAdminController {
    private final TenantRepository tenants;
    private final OrganizationRepository organizations;
    private final PublicSiteRepository publicSites;
    private final PublicSiteService publicSiteService;

    public PlatformAdminController(TenantRepository tenants, OrganizationRepository organizations,
                                   PublicSiteRepository publicSites, PublicSiteService publicSiteService) {
        this.tenants = tenants;
        this.organizations = organizations;
        this.publicSites = publicSites;
        this.publicSiteService = publicSiteService;
    }

    @GetMapping
    public List<TenantSummary> list() {
        return tenants.findAll().stream().map(t -> {
            PublicSite site = publicSites.findByTenantId(t.getId()).orElse(null);
            return new TenantSummary(t.getId(), t.getSlug(), t.getName(), t.getStatus(), t.isActive(),
                    site != null && site.isEnabled(), site != null && site.isPublished(), site == null ? t.getSlug() : site.getSiteSlug());
        }).toList();
    }

    @PostMapping
    @Transactional
    public Tenant create(@Valid @RequestBody TenantCreateRequest request) {
        String slug = request.slug().trim().toLowerCase(Locale.ROOT);
        if (tenants.findBySlugIgnoreCase(slug).isPresent()) throw new BusinessException("El slug ya está registrado");
        Tenant tenant = new Tenant();
        tenant.setSlug(slug);
        tenant.setName(request.name().trim());
        tenant = tenants.save(tenant);

        Organization org = new Organization();
        org.setTenantId(tenant.getId());
        org.setLegalName(request.legalName().trim());
        org.setTradeName(request.tradeName());
        org.setRuc(request.ruc());
        organizations.save(org);
        publicSiteService.ensureForTenant(tenant);
        return tenant;
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public Tenant changeStatus(@PathVariable UUID id, @RequestParam TenantStatus status) {
        Tenant tenant = tenants.findById(id).orElseThrow(() -> new BusinessException("Tenant no encontrado"));
        tenant.setStatus(status);
        tenant.setActive(status == TenantStatus.ACTIVE);
        return tenants.save(tenant);
    }

    public record TenantCreateRequest(@NotBlank String slug, @NotBlank String name,
                                      @NotBlank String legalName, String tradeName, String ruc) {}
    public record TenantSummary(UUID id, String slug, String name, TenantStatus status, boolean active,
                                boolean webEnabled, boolean webPublished, String publicSlug) {}
}
