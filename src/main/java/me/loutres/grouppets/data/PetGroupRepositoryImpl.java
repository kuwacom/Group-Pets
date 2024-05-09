package me.loutres.grouppets.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.NonNull;
import me.loutres.grouppets.Plugin;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PetGroupRepositoryImpl implements PetGroupRepository {
    private final Plugin plugin;
    public HashMap<UUID, PetGroupEntity> entities;

    public PetGroupRepositoryImpl(Plugin plugin) {
        this.plugin = plugin;
        if (!plugin.getDataFolder().isDirectory())
            plugin.getDataFolder().mkdir();
        Gson gson = new Gson();
        File jsonFile = new File(plugin.getDataFolder(), "pets.json");
        entities = new HashMap<>();
        if (jsonFile.isFile()) {
            try (FileReader reader = new FileReader(jsonFile)) {
                Type listType = new TypeToken<List<PetGroupEntity>>(){}.getType();
                List<PetGroupEntity> petGroups = gson.fromJson(reader, listType);
                if (petGroups==null)
                    return;
                for (PetGroupEntity petGroup : petGroups) {
                    entities.put(petGroup.getId(), petGroup);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void save(@NonNull PetGroupEntity entity) {
        entities.put(entity.getId(), entity.clone());
        write();
    }

    @Override
    @Nullable
    public PetGroupEntity find(@NonNull String name, @NonNull UUID owner) {
        return entities.values().stream()
                .filter(entity -> entity.getName().equals(name) && entity.getOwner().equals(owner))
                .map(PetGroupEntity::clone)
                .findFirst().orElse(null);
    }

    @Override
    @NonNull
    public List<PetGroupEntity> findByOwner(@NonNull UUID owner) {
        return entities.values().stream()
                .filter(entity -> entity.getOwner().equals(owner))
                .map(PetGroupEntity::clone)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(@NonNull PetGroupEntity entity) {
        entities.remove(entity.getId());
        write();
    }

    private void write() {
        File jsonFile = new File(plugin.getDataFolder(), "pets.json");
        Gson gson = new Gson();
        try {
            FileWriter writer = new FileWriter(jsonFile);
            gson.toJson(entities.values(), writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
