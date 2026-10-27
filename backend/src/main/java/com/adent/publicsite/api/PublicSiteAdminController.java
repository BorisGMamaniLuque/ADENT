package com.adent.publicsite.api;

import com.adent.platform.domain.Tenant;
import com.adent.platform.repository.TenantRepository;
import com.adent.publicsite.service.PublicSiteService;
import com.adent.publicsite.service.SiteProjectGeneratorService;
import com.adent.shared.exception.BusinessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/public-sites")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PublicSiteAdminController {
    private final PublicSiteService service;
    private final TenantRepository tenants;
    private final SiteProjectGeneratorService generator;

    public PublicSiteAdminController(PublicSiteService service, TenantRepository tenants, SiteProjectGeneratorService generator) {
        this.service = service;
        this.tenants = tenants;
        this.generator = generator;
    }

    @GetMapping("/tenant/{tenantId}")
    public PublicSiteService.EditorView get(@PathVariable UUID tenantId) {
        return service.editor(tenantId);
    }

    @PutMapping("/tenant/{tenantId}")
    public SaveResult save(@PathVariable UUID tenantId, @RequestBody PublicSiteService.EditorRequest request) {
        PublicSiteService.EditorView view = service.saveEditor(tenantId, request);
        Tenant tenant = tenants.findById(tenantId)
                .orElseThrow(() -> new BusinessException("Organización no encontrada"));

        String projectPath = generator.generate(
                tenant,
                view.siteSlug(),
                view.businessName() == null || view.businessName().isBlank() ? tenant.getName() : view.businessName());

        return new SaveResult(view, projectPath, true);
    }

    @PostMapping("/tenant/{tenantId}/generate-project")
    public GenerateResult generateProject(@PathVariable UUID tenantId) {
        Tenant tenant = tenants.findById(tenantId)
                .orElseThrow(() -> new BusinessException("Organización no encontrada"));
        PublicSiteService.EditorView view = service.editor(tenantId);
        String projectPath = generator.generate(
                tenant,
                view.siteSlug(),
                view.businessName() == null || view.businessName().isBlank() ? tenant.getName() : view.businessName());
        return new GenerateResult(projectPath, view.siteSlug(), true);
    }

    @GetMapping("/tenant/{tenantId}/project")
    public ProjectStatus projectStatus(@PathVariable UUID tenantId) {
        Tenant tenant = tenants.findById(tenantId)
                .orElseThrow(() -> new BusinessException("Organización no encontrada"));
        PublicSiteService.EditorView view = service.editor(tenantId);
        String slug = view.siteSlug() == null || view.siteSlug().isBlank() ? tenant.getSlug() : view.siteSlug();
        String path = "public-web/generated-sites/" + slug;
        return new ProjectStatus(slug, path, generator.exists(slug));
    }

    @PatchMapping("/tenant/{tenantId}/enabled")
    public PublicSiteService.EditorView toggle(@PathVariable UUID tenantId, @RequestParam boolean enabled) {
        return service.toggle(tenantId, enabled);
    }

    public record SaveResult(PublicSiteService.EditorView site, String projectPath, boolean projectGenerated) {}
    public record GenerateResult(String projectPath, String siteSlug, boolean generated) {}
    public record ProjectStatus(String siteSlug, String projectPath, boolean exists) {}
}
