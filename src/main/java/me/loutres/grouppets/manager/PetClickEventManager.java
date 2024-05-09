package me.loutres.grouppets.manager;

import me.loutres.grouppets.Plugin;
import me.loutres.grouppets.data.PetGroupEntity;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PetClickEventManager implements Listener {
    private final HashMap<Player, PetClickWaitHandler> petClickWaiting = new HashMap<>();
    private final Plugin plugin;

    public PetClickEventManager(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPetClick(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getRightClicked();

            if (wolf.isTamed() && wolf.getOwner().equals(event.getPlayer())) {
                if (petClickWaiting.containsKey(event.getPlayer())) {
                    PetClickWaitHandler handler = petClickWaiting.get(event.getPlayer());
                    petClickWaiting.remove(event.getPlayer());
                    handler.run(wolf);
                    return;
                }

                if (!event.getPlayer().isSneaking())
                    return;

                PetGroupEntity entity = getPetGroup(event.getPlayer(), wolf.getUniqueId());
                if (entity==null)
                    return;

                entity.getPets().stream()
                        .filter(pet -> !pet.equals(wolf.getUniqueId()))
                        .map(plugin.getServer()::getEntity)
                        .filter(Objects::nonNull)
                        .forEach(pet -> {
                            if (pet instanceof Wolf) {
                                Wolf wolfPet = (Wolf) pet;
                                wolfPet.setSitting(!wolf.isSitting());
                            } else if (pet instanceof Cat) {
                                Cat catPet = (Cat) pet;
                                catPet.setSitting(!wolf.isSitting());
                            }
                        });
            }
        } else if (event.getRightClicked() instanceof Cat) {
            Cat cat = (Cat) event.getRightClicked();
            if (cat.isTamed() && cat.getOwner().equals(event.getPlayer())) {
                if (petClickWaiting.containsKey(event.getPlayer())) {
                    PetClickWaitHandler handler = petClickWaiting.get(event.getPlayer());
                    petClickWaiting.remove(event.getPlayer());
                    handler.run(cat);
                    return;
                }

                if (!event.getPlayer().isSneaking())
                    return;

                PetGroupEntity entity = getPetGroup(event.getPlayer(), cat.getUniqueId());
                if (entity==null)
                    return;

                entity.getPets().stream()
                        .filter(pet -> !pet.equals(cat.getUniqueId()))
                        .map(plugin.getServer()::getEntity)
                        .filter(Objects::nonNull)
                        .forEach(pet -> {
                            if (pet instanceof Wolf) {
                                Wolf wolfPet = (Wolf) pet;
                                wolfPet.setSitting(!cat.isSitting());
                            } else if (pet instanceof Cat) {
                                Cat catPet = (Cat) pet;
                                catPet.setSitting(!cat.isSitting());
                            }
                        });
            }
        }
    }

    private PetGroupEntity getPetGroup(Player player, UUID entityUuid) {
        return plugin.petGroupRepository.findByOwner(player.getUniqueId()).stream()
                .filter(entity -> entity.getPets().contains(entityUuid))
                .findFirst().orElse(null);
    }

    public void waitPetClick(Player player, PetClickWaitHandler handler) {
        petClickWaiting.put(player, handler);
    }

    public abstract static class PetClickWaitHandler {
        public abstract void run(Entity entity);
    }
}
