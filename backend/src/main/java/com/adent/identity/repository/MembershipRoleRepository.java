package com.adent.identity.repository;
import com.adent.identity.domain.MembershipRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface MembershipRoleRepository extends JpaRepository<MembershipRole, UUID> {
    List<MembershipRole> findByMembershipId(UUID membershipId);
}
