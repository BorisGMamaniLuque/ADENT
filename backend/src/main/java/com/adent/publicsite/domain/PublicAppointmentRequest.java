package com.adent.publicsite.domain;
import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
@Entity @Table(name="public_appointment_requests")
public class PublicAppointmentRequest extends BaseEntity {
 @Column(name="tenant_id",nullable=false) private UUID tenantId;
 @Column(name="full_name",nullable=false,length=180) private String fullName;
 @Column(nullable=false,length=40) private String phone;
 @Column(length=180) private String email;
 @Column(name="service_name",length=180) private String serviceName;
 @Column(name="preferred_date") private LocalDate preferredDate;
 @Column(name="preferred_time",length=40) private String preferredTime;
 @Column(columnDefinition="text") private String message;
 @Column(nullable=false,length=30) private String status="PENDING";
 public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;}
 public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getServiceName(){return serviceName;} public void setServiceName(String v){serviceName=v;}
 public LocalDate getPreferredDate(){return preferredDate;} public void setPreferredDate(LocalDate v){preferredDate=v;}
 public String getPreferredTime(){return preferredTime;} public void setPreferredTime(String v){preferredTime=v;}
 public String getMessage(){return message;} public void setMessage(String v){message=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
