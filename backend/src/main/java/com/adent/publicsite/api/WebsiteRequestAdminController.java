package com.adent.publicsite.api;

import com.adent.platform.domain.Tenant;
import com.adent.platform.repository.TenantRepository;
import com.adent.publicsite.domain.*;
import com.adent.publicsite.repository.WebsiteRequestRepository;
import com.adent.publicsite.repository.PublicSiteRepository;
import com.adent.publicsite.service.PublicSiteService;
import com.adent.publicsite.service.SiteProjectGeneratorService;
import com.adent.shared.exception.BusinessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/platform/website-requests")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class WebsiteRequestAdminController {
    private final WebsiteRequestRepository requests;
    private final TenantRepository tenants;
    private final SiteProjectGeneratorService generator;
    private final PublicSiteService publicSiteService;
    private final PublicSiteRepository publicSites;

    public WebsiteRequestAdminController(WebsiteRequestRepository requests, TenantRepository tenants, SiteProjectGeneratorService generator, PublicSiteService publicSiteService, PublicSiteRepository publicSites){
        this.requests=requests;this.tenants=tenants;this.generator=generator;this.publicSiteService=publicSiteService;this.publicSites=publicSites;
    }

    @GetMapping
    public List<View> list(){return requests.findAllByOrderByCreatedAtDesc().stream().map(this::view).toList();}

    @GetMapping("/{id}")
    public View get(@PathVariable UUID id){return view(requests.findById(id).orElseThrow(() -> new BusinessException("Solicitud no encontrada")));}

    @PatchMapping("/{id}/review")
    @Transactional
    public View review(@PathVariable UUID id){WebsiteRequest r=req(id);r.setStatus(WebsiteRequestStatus.UNDER_REVIEW);return view(requests.save(r));}

    @PatchMapping("/{id}/needs-info")
    @Transactional
    public View needsInfo(@PathVariable UUID id,@RequestBody NoteRequest body){WebsiteRequest r=req(id);r.setStatus(WebsiteRequestStatus.NEEDS_INFO);r.setAdminNotes(body.note());return view(requests.save(r));}

    @PatchMapping("/{id}/reject")
    @Transactional
    public View reject(@PathVariable UUID id,@RequestBody NoteRequest body){WebsiteRequest r=req(id);r.setStatus(WebsiteRequestStatus.REJECTED);r.setAdminNotes(body.note());return view(requests.save(r));}

    @PostMapping("/{id}/approve-and-generate")
    @Transactional
    public View approveAndGenerate(@PathVariable UUID id){
        WebsiteRequest r=req(id); Tenant tenant=tenants.findById(r.getTenantId()).orElseThrow(() -> new BusinessException("Organización no encontrada"));
        r.setStatus(WebsiteRequestStatus.APPROVED); r.setApprovedAt(OffsetDateTime.now());
        PublicSite site=publicSiteService.ensureForTenant(tenant);
        site.setSiteSlug(tenant.getSlug());
        site.setBusinessName(r.getBusinessName()==null || r.getBusinessName().isBlank()?tenant.getName():r.getBusinessName());
        site.setTitle(site.getBusinessName()); site.setHeroTitle(site.getBusinessName());
        site.setAboutText(r.getDescription()); site.setPhone(r.getPhone()); site.setWhatsapp(r.getWhatsapp()); site.setEmail(r.getEmail()); site.setAddress(r.getAddress());
        site.setFacebookUrl(r.getFacebookUrl()); site.setInstagramUrl(r.getInstagramUrl()); site.setEnabled(true); site.setPublished(false);
        publicSites.save(site);
        String path=generator.generate(tenant,r); r.setProjectPath(path); r.setGeneratedAt(OffsetDateTime.now()); r.setStatus(WebsiteRequestStatus.GENERATED);
        return view(requests.save(r));
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public View status(@PathVariable UUID id,@RequestParam WebsiteRequestStatus status){WebsiteRequest r=req(id);r.setStatus(status);return view(requests.save(r));}

    private WebsiteRequest req(UUID id){return requests.findById(id).orElseThrow(() -> new BusinessException("Solicitud no encontrada"));}
    private View view(WebsiteRequest r){Tenant t=tenants.findById(r.getTenantId()).orElseThrow(() -> new BusinessException("Organización no encontrada"));return new View(r.getId(),t.getId(),t.getName(),t.getSlug(),r.getStatus().name(),r.getBusinessName(),r.getDescription(),r.getPreferredColors(),r.getServicesText(),r.getPhone(),r.getWhatsapp(),r.getEmail(),r.getAddress(),r.getFacebookUrl(),r.getInstagramUrl(),r.getDesignNotes(),r.getAdminNotes(),r.getProjectPath(),r.getRequestedAt(),r.getGeneratedAt());}
    public record NoteRequest(String note){}
    public record View(UUID id,UUID tenantId,String organizationName,String slug,String status,String businessName,String description,String preferredColors,String servicesText,String phone,String whatsapp,String email,String address,String facebookUrl,String instagramUrl,String designNotes,String adminNotes,String projectPath,OffsetDateTime requestedAt,OffsetDateTime generatedAt){}
}
