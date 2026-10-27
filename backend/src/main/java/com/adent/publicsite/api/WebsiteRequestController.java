package com.adent.publicsite.api;

import com.adent.platform.domain.Tenant;
import com.adent.platform.repository.TenantRepository;
import com.adent.publicsite.domain.*;
import com.adent.publicsite.repository.WebsiteRequestRepository;
import com.adent.shared.exception.BusinessException;
import com.adent.shared.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/my/website-request")
@PreAuthorize("hasRole('OWNER')")
public class WebsiteRequestController {
    private final WebsiteRequestRepository requests;
    private final TenantRepository tenants;
    private final CurrentUser currentUser;

    public WebsiteRequestController(WebsiteRequestRepository requests, TenantRepository tenants, CurrentUser currentUser) {
        this.requests=requests; this.tenants=tenants; this.currentUser=currentUser;
    }

    @GetMapping
    public View get() {
        UUID tenantId=currentUser.tenantId();
        Tenant tenant=tenants.findById(tenantId).orElseThrow(() -> new BusinessException("Organización no encontrada"));
        return requests.findByTenantId(tenantId).map(r -> toView(tenant,r)).orElseGet(() -> empty(tenant));
    }

    @PostMapping
    @Transactional
    public View submit(@RequestBody SaveRequest body) {
        UUID tenantId=currentUser.tenantId();
        Tenant tenant=tenants.findById(tenantId).orElseThrow(() -> new BusinessException("Organización no encontrada"));
        WebsiteRequest r=requests.findByTenantId(tenantId).orElseGet(WebsiteRequest::new);
        if (r.getStatus()==WebsiteRequestStatus.GENERATED || r.getStatus()==WebsiteRequestStatus.IN_DEVELOPMENT || r.getStatus()==WebsiteRequestStatus.READY || r.getStatus()==WebsiteRequestStatus.PUBLISHED)
            throw new BusinessException("El proyecto web ya fue generado. Los cambios de diseño se realizan en sus archivos físicos.");
        r.setTenantId(tenantId);
        r.setBusinessName(trim(body.businessName())); r.setDescription(trim(body.description())); r.setPreferredColors(trim(body.preferredColors()));
        r.setServicesText(trim(body.servicesText())); r.setPhone(trim(body.phone())); r.setWhatsapp(trim(body.whatsapp())); r.setEmail(trim(body.email()));
        r.setAddress(trim(body.address())); r.setFacebookUrl(trim(body.facebookUrl())); r.setInstagramUrl(trim(body.instagramUrl())); r.setDesignNotes(trim(body.designNotes()));
        r.setStatus(WebsiteRequestStatus.REQUESTED); r.setRequestedAt(OffsetDateTime.now()); r.setAdminNotes(null);
        return toView(tenant,requests.save(r));
    }

    private static String trim(String v){return v==null?null:v.trim();}
    private static View empty(Tenant t){return new View(t.getId(),t.getName(),t.getSlug(),null,"NOT_REQUESTED",null,null,null,null,null,null,null,null,null,null,null,null,null);}
    private static View toView(Tenant t, WebsiteRequest r){return new View(t.getId(),t.getName(),t.getSlug(),r.getId(),r.getStatus().name(),r.getBusinessName(),r.getDescription(),r.getPreferredColors(),r.getServicesText(),r.getPhone(),r.getWhatsapp(),r.getEmail(),r.getAddress(),r.getFacebookUrl(),r.getInstagramUrl(),r.getDesignNotes(),r.getAdminNotes(),r.getProjectPath());}

    public record SaveRequest(String businessName,String description,String preferredColors,String servicesText,String phone,String whatsapp,String email,String address,String facebookUrl,String instagramUrl,String designNotes){}
    public record View(UUID tenantId,String organizationName,String slug,UUID requestId,String status,String businessName,String description,String preferredColors,String servicesText,String phone,String whatsapp,String email,String address,String facebookUrl,String instagramUrl,String designNotes,String adminNotes,String projectPath){}
}
