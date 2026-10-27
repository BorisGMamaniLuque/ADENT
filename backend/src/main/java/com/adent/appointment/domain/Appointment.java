package com.adent.appointment.domain;
import com.adent.shared.domain.BaseEntity; import jakarta.persistence.*; import java.time.OffsetDateTime; import java.util.UUID;
@Entity @Table(name="appointments")
public class Appointment extends BaseEntity {
    @Column(name="tenant_id",nullable=false) private UUID tenantId; @Column(name="patient_id",nullable=false) private UUID patientId; @Column(name="staff_id",nullable=false) private UUID staffId; @Column(name="room_id") private UUID roomId;
    @Column(name="starts_at",nullable=false) private OffsetDateTime startsAt; @Column(name="ends_at") private OffsetDateTime endsAt; @Column(columnDefinition="text") private String reason;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private AppointmentStatus status=AppointmentStatus.SCHEDULED;
    public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;} public UUID getPatientId(){return patientId;} public void setPatientId(UUID v){patientId=v;} public UUID getStaffId(){return staffId;} public void setStaffId(UUID v){staffId=v;} public UUID getRoomId(){return roomId;} public void setRoomId(UUID v){roomId=v;} public OffsetDateTime getStartsAt(){return startsAt;} public void setStartsAt(OffsetDateTime v){startsAt=v;} public OffsetDateTime getEndsAt(){return endsAt;} public void setEndsAt(OffsetDateTime v){endsAt=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;} public AppointmentStatus getStatus(){return status;} public void setStatus(AppointmentStatus v){status=v;}
}
