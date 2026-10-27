package com.adent.structure.api;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public final class StructureDtos {
    private StructureDtos() {}
    public record BranchRequest(@NotBlank String name, String address) {}
    public record FloorRequest(UUID branchId, @NotBlank String name, Integer floorNumber) {}
    public record RoomRequest(UUID floorId, @NotBlank String name, String code) {}
}
