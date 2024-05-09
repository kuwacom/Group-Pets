package me.loutres.grouppets.data;

import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface PetGroupRepository {
    void save(@NonNull PetGroupEntity entity);
    PetGroupEntity find(@NonNull String name, @NonNull UUID owner);
    List<PetGroupEntity> findByOwner(@NonNull UUID owner);
    void delete(@NonNull PetGroupEntity entity);
}
