package com.adent.audit.service;
import com.adent.audit.domain.AuditLog; import com.adent.audit.repository.AuditLogRepository; import com.adent.shared.security.CurrentUser; import org.springframework.stereotype.Service; import java.util.UUID;
@Service
public class AuditService {
 private final AuditLogRepository repo; private final CurrentUser current;
 public AuditService(AuditLogRepository repo, CurrentUser current){this.repo=repo;this.current=current;}
 public void log(String action,String entityType,UUID entityId,String details){AuditLog a=new AuditLog();a.setTenantId(current.tenantId());a.setUsername(current.username());a.setAction(action);a.setEntityType(entityType);a.setEntityId(entityId);a.setDetails(details);repo.save(a);}
}
