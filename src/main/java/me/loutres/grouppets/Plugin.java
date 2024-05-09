package me.loutres.grouppets;

import co.aikar.commands.ConditionFailedException;
import lombok.NonNull;
import me.loutres.grouppets.command.GroupPetsCommand;
import co.aikar.commands.BukkitCommandManager;
import me.loutres.grouppets.data.PetGroupRepository;
import me.loutres.grouppets.data.PetGroupRepositoryImpl;
import me.loutres.grouppets.manager.PetClickEventManager;
import org.bukkit.plugin.java.JavaPlugin;


public final class Plugin extends JavaPlugin {
    public final PetGroupRepository petGroupRepository = new PetGroupRepositoryImpl(this);
    public final PetClickEventManager petClickEventManager = new PetClickEventManager(this);


    @Override
    public void onEnable() {
        getLogger().info("========================= " + getName() + " v" + getDescription().getVersion() + " =========================");
        BukkitCommandManager manager = new BukkitCommandManager(this);

        manager.registerDependency(PetGroupRepository.class, petGroupRepository);
        manager.registerDependency(PetClickEventManager.class, petClickEventManager);

        manager.getCommandConditions().addCondition(String.class, "groupName", (context, execCommand, value) -> {
            if (value == null)
                return;
            if (!value.matches("^[a-z0-9]*$"))
                throw new ConditionFailedException("グループ名は小文字アルファベットと数字のみを含めることができます。");
        });

        manager.registerCommand(new GroupPetsCommand());

        getServer().getPluginManager().registerEvents(petClickEventManager, this);
    }
}
