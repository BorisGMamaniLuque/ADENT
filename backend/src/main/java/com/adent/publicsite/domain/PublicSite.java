package com.adent.publicsite.domain;

import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "public_sites")
public class PublicSite extends BaseEntity {
    @Column(name = "tenant_id", nullable = false, unique = true)
    private UUID tenantId;
    @Column(nullable = false)
    private boolean enabled;
    @Column(nullable = false)
    private boolean published;
    @Column(name = "site_slug", length = 100)
    private String siteSlug;
    @Column(name = "template_key", length = 40)
    private String templateKey = "MODERN";
    @Column(length = 180)
    private String title;
    @Column(name = "business_name", length = 180)
    private String businessName;
    @Column(name = "logo_url", length = 500)
    private String logoUrl;
    @Column(name = "primary_color", length = 20)
    private String primaryColor = "#0f766e";
    @Column(name = "secondary_color", length = 20)
    private String secondaryColor = "#0f172a";
    @Column(name = "hero_title", length = 220)
    private String heroTitle;
    @Column(name = "hero_subtitle", length = 500)
    private String heroSubtitle;
    @Column(name = "hero_image_url", length = 500)
    private String heroImageUrl;
    @Column(name = "about_title", length = 180)
    private String aboutTitle;
    @Column(name = "about_text", columnDefinition = "text")
    private String aboutText;
    @Column(columnDefinition = "text")
    private String mission;
    @Column(columnDefinition = "text")
    private String vision;
    @Column(length = 40)
    private String phone;
    @Column(length = 40)
    private String whatsapp;
    @Column(length = 180)
    private String email;
    @Column(length = 300)
    private String address;
    @Column(name = "schedule_text", length = 250)
    private String scheduleText;
    @Column(name = "map_url", length = 700)
    private String mapUrl;
    @Column(name = "facebook_url", length = 500)
    private String facebookUrl;
    @Column(name = "instagram_url", length = 500)
    private String instagramUrl;
    @Column(name = "tiktok_url", length = 500)
    private String tiktokUrl;

    public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;}
    public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;}
    public boolean isPublished(){return published;} public void setPublished(boolean v){published=v;}
    public String getSiteSlug(){return siteSlug;} public void setSiteSlug(String v){siteSlug=v;}
    public String getTemplateKey(){return templateKey;} public void setTemplateKey(String v){templateKey=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getBusinessName(){return businessName;} public void setBusinessName(String v){businessName=v;}
    public String getLogoUrl(){return logoUrl;} public void setLogoUrl(String v){logoUrl=v;}
    public String getPrimaryColor(){return primaryColor;} public void setPrimaryColor(String v){primaryColor=v;}
    public String getSecondaryColor(){return secondaryColor;} public void setSecondaryColor(String v){secondaryColor=v;}
    public String getHeroTitle(){return heroTitle;} public void setHeroTitle(String v){heroTitle=v;}
    public String getHeroSubtitle(){return heroSubtitle;} public void setHeroSubtitle(String v){heroSubtitle=v;}
    public String getHeroImageUrl(){return heroImageUrl;} public void setHeroImageUrl(String v){heroImageUrl=v;}
    public String getAboutTitle(){return aboutTitle;} public void setAboutTitle(String v){aboutTitle=v;}
    public String getAboutText(){return aboutText;} public void setAboutText(String v){aboutText=v;}
    public String getMission(){return mission;} public void setMission(String v){mission=v;}
    public String getVision(){return vision;} public void setVision(String v){vision=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getWhatsapp(){return whatsapp;} public void setWhatsapp(String v){whatsapp=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getScheduleText(){return scheduleText;} public void setScheduleText(String v){scheduleText=v;}
    public String getMapUrl(){return mapUrl;} public void setMapUrl(String v){mapUrl=v;}
    public String getFacebookUrl(){return facebookUrl;} public void setFacebookUrl(String v){facebookUrl=v;}
    public String getInstagramUrl(){return instagramUrl;} public void setInstagramUrl(String v){instagramUrl=v;}
    public String getTiktokUrl(){return tiktokUrl;} public void setTiktokUrl(String v){tiktokUrl=v;}
}
