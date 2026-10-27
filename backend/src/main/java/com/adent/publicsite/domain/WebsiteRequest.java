package com.adent.publicsite.domain;

import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "website_requests")
public class WebsiteRequest extends BaseEntity {
    @Column(name="tenant_id", nullable=false, unique=true)
    private UUID tenantId;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    private WebsiteRequestStatus status = WebsiteRequestStatus.REQUESTED;
    @Column(name="business_name", length=180)
    private String businessName;
    @Column(columnDefinition="text")
    private String description;
    @Column(name="preferred_colors", length=180)
    private String preferredColors;
    @Column(name="services_text", columnDefinition="text")
    private String servicesText;
    @Column(length=40) private String phone;
    @Column(length=40) private String whatsapp;
    @Column(length=180) private String email;
    @Column(length=300) private String address;
    @Column(name="facebook_url", length=500) private String facebookUrl;
    @Column(name="instagram_url", length=500) private String instagramUrl;
    @Column(name="design_notes", columnDefinition="text") private String designNotes;
    @Column(name="admin_notes", columnDefinition="text") private String adminNotes;
    @Column(name="project_path", length=700) private String projectPath;
    @Column(name="requested_at") private OffsetDateTime requestedAt;
    @Column(name="approved_at") private OffsetDateTime approvedAt;
    @Column(name="generated_at") private OffsetDateTime generatedAt;

    public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;}
    public WebsiteRequestStatus getStatus(){return status;} public void setStatus(WebsiteRequestStatus v){status=v;}
    public String getBusinessName(){return businessName;} public void setBusinessName(String v){businessName=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getPreferredColors(){return preferredColors;} public void setPreferredColors(String v){preferredColors=v;}
    public String getServicesText(){return servicesText;} public void setServicesText(String v){servicesText=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getWhatsapp(){return whatsapp;} public void setWhatsapp(String v){whatsapp=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getFacebookUrl(){return facebookUrl;} public void setFacebookUrl(String v){facebookUrl=v;}
    public String getInstagramUrl(){return instagramUrl;} public void setInstagramUrl(String v){instagramUrl=v;}
    public String getDesignNotes(){return designNotes;} public void setDesignNotes(String v){designNotes=v;}
    public String getAdminNotes(){return adminNotes;} public void setAdminNotes(String v){adminNotes=v;}
    public String getProjectPath(){return projectPath;} public void setProjectPath(String v){projectPath=v;}
    public OffsetDateTime getRequestedAt(){return requestedAt;} public void setRequestedAt(OffsetDateTime v){requestedAt=v;}
    public OffsetDateTime getApprovedAt(){return approvedAt;} public void setApprovedAt(OffsetDateTime v){approvedAt=v;}
    public OffsetDateTime getGeneratedAt(){return generatedAt;} public void setGeneratedAt(OffsetDateTime v){generatedAt=v;}
}
