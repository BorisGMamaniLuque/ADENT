package com.adent.publicsite.domain;
import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="public_site_gallery")
public class PublicGalleryItem extends BaseEntity {
 @Column(name="tenant_id",nullable=false) private UUID tenantId;
 @Column(length=180) private String title;
 @Column(length=500) private String description;
 @Column(length=100) private String category;
 @Column(name="image_url",nullable=false,length=500) private String imageUrl;
 @Column(name="display_order",nullable=false) private int displayOrder;
 @Column(nullable=false) private boolean active=true;
 public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;}
 public String getTitle(){return title;} public void setTitle(String v){title=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getCategory(){return category;} public void setCategory(String v){category=v;}
 public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
 public int getDisplayOrder(){return displayOrder;} public void setDisplayOrder(int v){displayOrder=v;}
 public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
