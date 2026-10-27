package com.adent.publicsite.domain;
import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
@Entity @Table(name="public_site_services")
public class PublicService extends BaseEntity {
 @Column(name="tenant_id",nullable=false) private UUID tenantId;
 @Column(nullable=false,length=160) private String name;
 @Column(length=500) private String summary;
 @Column(columnDefinition="text") private String description;
 @Column(name="image_url",length=500) private String imageUrl;
 @Column(name="price_from",precision=12,scale=2) private BigDecimal priceFrom;
 @Column(name="display_order",nullable=false) private int displayOrder;
 @Column(nullable=false) private boolean featured;
 @Column(nullable=false) private boolean active=true;
 public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;}
 public String getName(){return name;} public void setName(String v){name=v;}
 public String getSummary(){return summary;} public void setSummary(String v){summary=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
 public BigDecimal getPriceFrom(){return priceFrom;} public void setPriceFrom(BigDecimal v){priceFrom=v;}
 public int getDisplayOrder(){return displayOrder;} public void setDisplayOrder(int v){displayOrder=v;}
 public boolean isFeatured(){return featured;} public void setFeatured(boolean v){featured=v;}
 public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
