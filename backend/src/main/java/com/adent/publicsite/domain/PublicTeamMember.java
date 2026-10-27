package com.adent.publicsite.domain;
import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="public_site_team")
public class PublicTeamMember extends BaseEntity {
 @Column(name="tenant_id",nullable=false) private UUID tenantId;
 @Column(name="full_name",nullable=false,length=180) private String fullName;
 @Column(name="professional_title",length=160) private String professionalTitle;
 @Column(length=180) private String specialty;
 @Column(name="license_number",length=80) private String licenseNumber;
 @Column(columnDefinition="text") private String bio;
 @Column(name="image_url",length=500) private String imageUrl;
 @Column(name="display_order",nullable=false) private int displayOrder;
 @Column(nullable=false) private boolean featured;
 @Column(nullable=false) private boolean active=true;
 public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;}
 public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
 public String getProfessionalTitle(){return professionalTitle;} public void setProfessionalTitle(String v){professionalTitle=v;}
 public String getSpecialty(){return specialty;} public void setSpecialty(String v){specialty=v;}
 public String getLicenseNumber(){return licenseNumber;} public void setLicenseNumber(String v){licenseNumber=v;}
 public String getBio(){return bio;} public void setBio(String v){bio=v;}
 public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
 public int getDisplayOrder(){return displayOrder;} public void setDisplayOrder(int v){displayOrder=v;}
 public boolean isFeatured(){return featured;} public void setFeatured(boolean v){featured=v;}
 public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
