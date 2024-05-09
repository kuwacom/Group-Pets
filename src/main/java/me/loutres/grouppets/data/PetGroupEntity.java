package me.loutres.grouppets.data;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PetGroupEntity implements Cloneable {
    @NonNull
    private UUID id;
    @NonNull
    private String name;
    @NonNull
    private UUID owner;
    @NonNull
    private List<UUID> pets;

    public PetGroupEntity(@NonNull String name, @NonNull UUID owner) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.owner = owner;
        this.pets = new ArrayList<>();
    }

    public PetGroupEntity(){}

    public void addPet(UUID pet) {
        pets.add(pet);
    }

    public void removePet(UUID pet) {
        pets.remove(pet);
    }

    @Override
    @NonNull
    public PetGroupEntity clone() {
        try {
            PetGroupEntity clone = (PetGroupEntity) super.clone();
            clone.name = name;
            clone.owner = owner;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
