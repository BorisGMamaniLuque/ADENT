package com.adent.publicsite.service;

import com.adent.platform.domain.Tenant;
import com.adent.platform.repository.TenantRepository;
import com.adent.publicsite.domain.*;
import com.adent.publicsite.repository.*;
import com.adent.shared.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class PublicSiteService {
    private final PublicSiteRepository sites;
    private final PublicServiceRepository services;
    private final PublicTeamRepository team;
    private final PublicGalleryRepository gallery;
    private final PublicAppointmentRequestRepository requests;
    private final TenantRepository tenants;

    public PublicSiteService(PublicSiteRepository sites, PublicServiceRepository services,
                             PublicTeamRepository team, PublicGalleryRepository gallery,
                             PublicAppointmentRequestRepository requests, TenantRepository tenants) {
        this.sites = sites;
        this.services = services;
        this.team = team;
        this.gallery = gallery;
        this.requests = requests;
        this.tenants = tenants;
    }

    @Transactional
    public PublicSite ensureForTenant(Tenant tenant) {
        return sites.findByTenantId(tenant.getId()).orElseGet(() -> {
            PublicSite site = new PublicSite();
            site.setTenantId(tenant.getId());
            site.setSiteSlug(tenant.getSlug());
            site.setTitle(tenant.getName());
            site.setBusinessName(tenant.getName());
            site.setHeroTitle(tenant.getName());
            site.setHeroSubtitle("Atención profesional y cercana para nuestros pacientes.");
            site.setEnabled(false);
            site.setPublished(false);
            return sites.save(site);
        });
    }

    @Transactional(readOnly = true)
    public EditorView editor(UUID tenantId) {
        Tenant tenant = tenants.findById(tenantId).orElseThrow(() -> new BusinessException("Organización no encontrada"));
        PublicSite site = sites.findByTenantId(tenantId).orElse(null);
        if (site == null) {
            site = new PublicSite();
            site.setTenantId(tenantId);
            site.setSiteSlug(tenant.getSlug());
            site.setTitle(tenant.getName());
            site.setBusinessName(tenant.getName());
        }
        return toEditor(site,
                services.findByTenantIdOrderByDisplayOrderAscNameAsc(tenantId),
                team.findByTenantIdOrderByDisplayOrderAscFullNameAsc(tenantId),
                gallery.findByTenantIdOrderByDisplayOrderAsc(tenantId));
    }

    @Transactional
    public EditorView saveEditor(UUID tenantId, EditorRequest request) {
        Tenant tenant = tenants.findById(tenantId).orElseThrow(() -> new BusinessException("Organización no encontrada"));
        String slug = normalizeSlug(request.siteSlug() == null || request.siteSlug().isBlank() ? tenant.getSlug() : request.siteSlug());
        sites.findBySiteSlugIgnoreCase(slug).filter(s -> !tenantId.equals(s.getTenantId()))
                .ifPresent(s -> { throw new BusinessException("El slug público ya está siendo utilizado"); });

        PublicSite site = sites.findByTenantId(tenantId).orElseGet(PublicSite::new);
        site.setTenantId(tenantId);
        site.setSiteSlug(slug);
        site.setTemplateKey(defaultText(request.templateKey(), "MODERN"));
        site.setEnabled(request.enabled());
        site.setPublished(request.published() && request.enabled());
        site.setTitle(trim(request.title()));
        site.setBusinessName(trim(request.businessName()));
        site.setLogoUrl(trim(request.logoUrl()));
        site.setPrimaryColor(defaultText(request.primaryColor(), "#0f766e"));
        site.setSecondaryColor(defaultText(request.secondaryColor(), "#0f172a"));
        site.setHeroTitle(trim(request.heroTitle()));
        site.setHeroSubtitle(trim(request.heroSubtitle()));
        site.setHeroImageUrl(trim(request.heroImageUrl()));
        site.setAboutTitle(trim(request.aboutTitle()));
        site.setAboutText(trim(request.aboutText()));
        site.setMission(trim(request.mission()));
        site.setVision(trim(request.vision()));
        site.setPhone(trim(request.phone()));
        site.setWhatsapp(trim(request.whatsapp()));
        site.setEmail(trim(request.email()));
        site.setAddress(trim(request.address()));
        site.setScheduleText(trim(request.scheduleText()));
        site.setMapUrl(trim(request.mapUrl()));
        site.setFacebookUrl(trim(request.facebookUrl()));
        site.setInstagramUrl(trim(request.instagramUrl()));
        site.setTiktokUrl(trim(request.tiktokUrl()));
        site = sites.save(site);

        services.deleteByTenantId(tenantId);
        team.deleteByTenantId(tenantId);
        gallery.deleteByTenantId(tenantId);

        if (request.services() != null) {
            int order = 0;
            for (ServiceItem item : request.services()) {
                if (item.name() == null || item.name().isBlank()) continue;
                PublicService e = new PublicService();
                e.setTenantId(tenantId); e.setName(item.name().trim()); e.setSummary(trim(item.summary()));
                e.setDescription(trim(item.description())); e.setImageUrl(trim(item.imageUrl())); e.setPriceFrom(item.priceFrom());
                e.setDisplayOrder(item.displayOrder() == null ? order++ : item.displayOrder());
                e.setFeatured(Boolean.TRUE.equals(item.featured())); e.setActive(item.active() == null || item.active());
                services.save(e);
            }
        }
        if (request.team() != null) {
            int order = 0;
            for (TeamItem item : request.team()) {
                if (item.fullName() == null || item.fullName().isBlank()) continue;
                PublicTeamMember e = new PublicTeamMember();
                e.setTenantId(tenantId); e.setFullName(item.fullName().trim()); e.setProfessionalTitle(trim(item.professionalTitle()));
                e.setSpecialty(trim(item.specialty())); e.setLicenseNumber(trim(item.licenseNumber())); e.setBio(trim(item.bio()));
                e.setImageUrl(trim(item.imageUrl())); e.setDisplayOrder(item.displayOrder() == null ? order++ : item.displayOrder());
                e.setFeatured(Boolean.TRUE.equals(item.featured())); e.setActive(item.active() == null || item.active());
                team.save(e);
            }
        }
        if (request.gallery() != null) {
            int order = 0;
            for (GalleryItem item : request.gallery()) {
                if (item.imageUrl() == null || item.imageUrl().isBlank()) continue;
                PublicGalleryItem e = new PublicGalleryItem();
                e.setTenantId(tenantId); e.setTitle(trim(item.title())); e.setDescription(trim(item.description()));
                e.setCategory(trim(item.category())); e.setImageUrl(item.imageUrl().trim());
                e.setDisplayOrder(item.displayOrder() == null ? order++ : item.displayOrder()); e.setActive(item.active() == null || item.active());
                gallery.save(e);
            }
        }
        return editor(tenantId);
    }

    @Transactional
    public EditorView toggle(UUID tenantId, boolean enabled) {
        Tenant tenant = tenants.findById(tenantId).orElseThrow(() -> new BusinessException("Organización no encontrada"));
        PublicSite site = ensureForTenant(tenant);
        site.setEnabled(enabled);
        if (!enabled) site.setPublished(false);
        sites.save(site);
        return editor(tenantId);
    }

    @Transactional(readOnly = true)
    public PublicBundle publicBundle(String slug) {
        PublicSite site = sites.findBySiteSlugIgnoreCase(normalizeSlug(slug))
                .orElseThrow(() -> new BusinessException("Sitio público no encontrado"));
        if (!site.isEnabled() || !site.isPublished()) throw new BusinessException("Sitio público no disponible");
        return new PublicBundle(site.getSiteSlug(), site.getTemplateKey(), site.getBusinessName(), site.getLogoUrl(), site.getPrimaryColor(), site.getSecondaryColor(),
                site.getHeroTitle(), site.getHeroSubtitle(), site.getHeroImageUrl(), site.getAboutTitle(), site.getAboutText(), site.getMission(), site.getVision(),
                site.getPhone(), site.getWhatsapp(), site.getEmail(), site.getAddress(), site.getScheduleText(), site.getMapUrl(), site.getFacebookUrl(), site.getInstagramUrl(), site.getTiktokUrl(),
                services.findByTenantIdAndActiveTrueOrderByDisplayOrderAscNameAsc(site.getTenantId()).stream().map(PublicSiteService::toService).toList(),
                team.findByTenantIdAndActiveTrueOrderByDisplayOrderAscFullNameAsc(site.getTenantId()).stream().map(PublicSiteService::toTeam).toList(),
                gallery.findByTenantIdAndActiveTrueOrderByDisplayOrderAsc(site.getTenantId()).stream().map(PublicSiteService::toGallery).toList());
    }

    @Transactional
    public RequestResult createAppointmentRequest(String slug, AppointmentRequestInput input) {
        PublicSite site = sites.findBySiteSlugIgnoreCase(normalizeSlug(slug))
                .orElseThrow(() -> new BusinessException("Sitio público no encontrado"));
        if (!site.isEnabled() || !site.isPublished()) throw new BusinessException("Sitio público no disponible");
        if (input.fullName() == null || input.fullName().isBlank() || input.phone() == null || input.phone().isBlank())
            throw new BusinessException("Nombre y teléfono son obligatorios");
        PublicAppointmentRequest e = new PublicAppointmentRequest();
        e.setTenantId(site.getTenantId()); e.setFullName(input.fullName().trim()); e.setPhone(input.phone().trim());
        e.setEmail(trim(input.email())); e.setServiceName(trim(input.serviceName())); e.setPreferredDate(input.preferredDate());
        e.setPreferredTime(trim(input.preferredTime())); e.setMessage(trim(input.message()));
        e = requests.save(e);
        return new RequestResult(e.getId(), e.getStatus(), "Solicitud recibida. Recepción podrá confirmarla posteriormente.");
    }

    private static String normalizeSlug(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9-]", "-").replaceAll("-+", "-").replaceAll("^-|-$", "");
    }
    private static String trim(String v){ return v == null ? null : v.trim(); }
    private static String defaultText(String v,String d){ return v==null||v.isBlank()?d:v.trim(); }

    private EditorView toEditor(PublicSite s, List<PublicService> sv, List<PublicTeamMember> tm, List<PublicGalleryItem> gl) {
        return new EditorView(s.getTenantId(), s.isEnabled(), s.isPublished(), s.getSiteSlug(), s.getTemplateKey(), s.getTitle(), s.getBusinessName(), s.getLogoUrl(),
                s.getPrimaryColor(), s.getSecondaryColor(), s.getHeroTitle(), s.getHeroSubtitle(), s.getHeroImageUrl(), s.getAboutTitle(), s.getAboutText(), s.getMission(), s.getVision(),
                s.getPhone(), s.getWhatsapp(), s.getEmail(), s.getAddress(), s.getScheduleText(), s.getMapUrl(), s.getFacebookUrl(), s.getInstagramUrl(), s.getTiktokUrl(),
                sv.stream().map(PublicSiteService::toService).toList(), tm.stream().map(PublicSiteService::toTeam).toList(), gl.stream().map(PublicSiteService::toGallery).toList());
    }
    private static ServiceItem toService(PublicService e){return new ServiceItem(e.getId(),e.getName(),e.getSummary(),e.getDescription(),e.getImageUrl(),e.getPriceFrom(),e.getDisplayOrder(),e.isFeatured(),e.isActive());}
    private static TeamItem toTeam(PublicTeamMember e){return new TeamItem(e.getId(),e.getFullName(),e.getProfessionalTitle(),e.getSpecialty(),e.getLicenseNumber(),e.getBio(),e.getImageUrl(),e.getDisplayOrder(),e.isFeatured(),e.isActive());}
    private static GalleryItem toGallery(PublicGalleryItem e){return new GalleryItem(e.getId(),e.getTitle(),e.getDescription(),e.getCategory(),e.getImageUrl(),e.getDisplayOrder(),e.isActive());}

    public record EditorRequest(boolean enabled, boolean published, String siteSlug, String templateKey, String title, String businessName, String logoUrl,
                                String primaryColor, String secondaryColor, String heroTitle, String heroSubtitle, String heroImageUrl,
                                String aboutTitle, String aboutText, String mission, String vision, String phone, String whatsapp, String email,
                                String address, String scheduleText, String mapUrl, String facebookUrl, String instagramUrl, String tiktokUrl,
                                List<ServiceItem> services, List<TeamItem> team, List<GalleryItem> gallery) {}
    public record EditorView(UUID tenantId, boolean enabled, boolean published, String siteSlug, String templateKey, String title, String businessName, String logoUrl,
                             String primaryColor, String secondaryColor, String heroTitle, String heroSubtitle, String heroImageUrl,
                             String aboutTitle, String aboutText, String mission, String vision, String phone, String whatsapp, String email,
                             String address, String scheduleText, String mapUrl, String facebookUrl, String instagramUrl, String tiktokUrl,
                             List<ServiceItem> services, List<TeamItem> team, List<GalleryItem> gallery) {}
    public record ServiceItem(UUID id, String name, String summary, String description, String imageUrl, BigDecimal priceFrom, Integer displayOrder, Boolean featured, Boolean active) {}
    public record TeamItem(UUID id, String fullName, String professionalTitle, String specialty, String licenseNumber, String bio, String imageUrl, Integer displayOrder, Boolean featured, Boolean active) {}
    public record GalleryItem(UUID id, String title, String description, String category, String imageUrl, Integer displayOrder, Boolean active) {}
    public record PublicBundle(String siteSlug, String templateKey, String businessName, String logoUrl, String primaryColor, String secondaryColor, String heroTitle, String heroSubtitle,
                               String heroImageUrl, String aboutTitle, String aboutText, String mission, String vision, String phone, String whatsapp, String email,
                               String address, String scheduleText, String mapUrl, String facebookUrl, String instagramUrl, String tiktokUrl,
                               List<ServiceItem> services, List<TeamItem> team, List<GalleryItem> gallery) {}
    public record AppointmentRequestInput(String fullName, String phone, String email, String serviceName, LocalDate preferredDate, String preferredTime, String message) {}
    public record RequestResult(UUID id, String status, String message) {}
}
