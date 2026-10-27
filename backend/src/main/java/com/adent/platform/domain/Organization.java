package com.adent.platform.domain;

import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {
    @Column(name = "tenant_id", nullable = false, unique = true)
    private UUID tenantId;
    @Column(name = "legal_name", nullable = false, length = 180)
    private String legalName;
    @Column(name = "trade_name", length = 180)
    private String tradeName;
    @Column(name = "establishment_type", nullable = false, length = 40)
    private String establishmentType = "CLINIC";
    @Column(length = 20)
    private String ruc;
    @Column(length = 30)
    private String phone;
    @Column(length = 160)
    private String email;
    @Column(length = 250)
    private String address;
    @Column(name = "logo_text", length = 16)
    private String logoText;
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public String getTradeName() { return tradeName; }
    public void setTradeName(String tradeName) { this.tradeName = tradeName; }
    public String getEstablishmentType() { return establishmentType; }
    public void setEstablishmentType(String establishmentType) { this.establishmentType = establishmentType; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getLogoText() { return logoText; }
    public void setLogoText(String logoText) { this.logoText = logoText; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
}
