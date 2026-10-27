package com.adent.structure.repository;
import com.adent.structure.domain.Room; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface RoomRepository extends JpaRepository<Room, UUID>{ List<Room> findByTenantIdOrderByName(UUID tenantId); Optional<Room> findByIdAndTenantId(UUID id, UUID tenantId); }
