package com.adent.structure.api;

import com.adent.shared.exception.NotFoundException;
import com.adent.shared.security.CurrentUser;
import com.adent.structure.domain.*;
import com.adent.structure.repository.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/structure")
@PreAuthorize("hasAnyRole('OWNER','ADMIN')")
public class StructureController {
    private final CurrentUser current;
    private final BranchRepository branches;
    private final FloorRepository floors;
    private final RoomRepository rooms;

    public StructureController(CurrentUser current, BranchRepository branches, FloorRepository floors, RoomRepository rooms) {
        this.current = current;
        this.branches = branches;
        this.floors = floors;
        this.rooms = rooms;
    }

    @GetMapping("/branches")
    public List<Branch> branches() {
        return branches.findByTenantIdOrderByName(current.tenantId());
    }

    @PostMapping("/branches")
    public Branch createBranch(@Valid @RequestBody StructureDtos.BranchRequest r) {
        Branch e = new Branch();
        e.setTenantId(current.tenantId());
        e.setName(r.name());
        e.setAddress(r.address());
        return branches.save(e);
    }

    @GetMapping("/floors")
    public List<Floor> floors(@RequestParam(required = false) UUID branchId) {
        UUID tenantId = current.tenantId();
        if (branchId == null) return floors.findByTenantIdOrderByFloorNumberAsc(tenantId);
        branches.findByIdAndTenantId(branchId, tenantId)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada"));
        return floors.findByTenantIdAndBranchIdOrderByFloorNumberAsc(tenantId, branchId);
    }

    @PostMapping("/floors")
    public Floor createFloor(@Valid @RequestBody StructureDtos.FloorRequest r) {
        UUID tenantId = current.tenantId();
        branches.findByIdAndTenantId(r.branchId(), tenantId)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada"));
        Floor e = new Floor();
        e.setTenantId(tenantId);
        e.setBranchId(r.branchId());
        e.setName(r.name());
        e.setFloorNumber(r.floorNumber());
        return floors.save(e);
    }

    @GetMapping("/rooms")
    public List<Room> rooms() {
        return rooms.findByTenantIdOrderByName(current.tenantId());
    }

    @PostMapping("/rooms")
    public Room createRoom(@Valid @RequestBody StructureDtos.RoomRequest r) {
        UUID tenantId = current.tenantId();
        floors.findByIdAndTenantId(r.floorId(), tenantId)
                .orElseThrow(() -> new NotFoundException("Piso no encontrado"));
        Room e = new Room();
        e.setTenantId(tenantId);
        e.setFloorId(r.floorId());
        e.setName(r.name());
        e.setCode(r.code());
        return rooms.save(e);
    }
}
