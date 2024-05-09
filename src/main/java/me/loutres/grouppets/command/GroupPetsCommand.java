package me.loutres.grouppets.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.loutres.grouppets.Plugin;
import me.loutres.grouppets.data.PetGroupEntity;
import me.loutres.grouppets.data.PetGroupRepository;
import me.loutres.grouppets.manager.PetClickEventManager;
import me.loutres.grouppets.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("grouppets")
public class GroupPetsCommand extends BaseCommand {

    @Dependency
    private PetGroupRepository petGroupRepository;
    @Dependency
    private PetClickEventManager petClickEventManager;
    @Dependency
    Plugin plugin;

    @Subcommand("create group")
    @CommandPermission("grouppets.create.group")
    private void createGroup(Player player, @Conditions("groupName") String groupName) {
        PetGroupEntity entity = petGroupRepository.find(groupName, player.getUniqueId());
        if (entity != null) {
            Util.sendErrorMessage(player, "グループ " + groupName + " は既に存在します。");
            return;
        }

        entity = new PetGroupEntity(groupName, player.getUniqueId());
        petGroupRepository.save(entity);

        Util.sendSuccessMessage(player, "グループ " + groupName + " を作成しました。");
    }

    @Subcommand("delete group")
    @CommandPermission("grouppets.delete.group")
    private void deleteGroup(Player player, @Conditions("groupName") String groupName) {
        PetGroupEntity entity = getPetGroup(player, groupName);
        if (entity == null)
            return;

        petGroupRepository.delete(entity);
        Util.sendSuccessMessage(player, "グループ " + groupName + " を削除しました。");
    }

    @Subcommand("group add")
    @CommandPermission("grouppets.group.add")
    private void addGroup(Player player, @Conditions("groupName") String groupName) {
        PetGroupEntity entity = getPetGroup(player, groupName);
        if (entity == null)
            return;

        Util.sendSuccessMessage(player, "ペットを右クリックしてグループ " + groupName + " に追加してください。");

        petClickEventManager.waitPetClick(player, new PetClickEventManager.PetClickWaitHandler() {
                    @Override
                    public void run(Entity pet) {
                        if (entity.getPets().contains(pet.getUniqueId())) {
                            Util.sendErrorMessage(player, "ペットは既にグループ " + groupName + " に追加されています。");
                            return;
                        }

                        entity.addPet(pet.getUniqueId());
                        petGroupRepository.save(entity);
                        Util.sendSuccessMessage(player, "ペットをグループ " + groupName + " に追加しました。");
                    }
                }
        );
    }

    @Subcommand("group remove")
    @CommandPermission("grouppets.group.remove")
    private void removeGroup(Player player, @Conditions("groupName") String groupName) {
        PetGroupEntity entity = getPetGroup(player, groupName);
        if (entity == null)
            return;

        Util.sendSuccessMessage(player, "ペットを右クリックしてグループ " + groupName + " から削除してください。");

        petClickEventManager.waitPetClick(player, new PetClickEventManager.PetClickWaitHandler() {
                    @Override
                    public void run(Entity pet) {
                        if (!entity.getPets().contains(pet.getUniqueId())) {
                            Util.sendErrorMessage(player, "ペットはグループ " + groupName + " に追加されていません。");
                            return;
                        }

                        entity.removePet(pet.getUniqueId());
                        petGroupRepository.save(entity);
                        Util.sendSuccessMessage(player, "ペットをグループ " + groupName + " から削除しました。");
                    }
                }
        );
    }

    @Subcommand("group list")
    @CommandPermission("grouppets.group.list")
    private void listGroup(Player player) {
        Util.sendSuccessMessage(player,
                petGroupRepository.findByOwner(player.getUniqueId()).stream()
                        .map(pet -> pet.getName()+" ("+pet.getPets().size()+"匹)")
                        .collect(Collectors.joining(", ")));
    }

    @Subcommand("group list")
    @CommandPermission("grouppets.group.list")
    private void listGroup(Player player, @Conditions("groupName") String groupName) {
        PetGroupEntity entity = getPetGroup(player, groupName);
        if (entity == null)
            return;

        StringBuilder sb = new StringBuilder();
        sb.append("グループ ").append(entity.getName()).append(" のペット:\n");

        for (UUID petId : entity.getPets()) {
            Entity pet = plugin.getServer().getEntity(petId);
            if (pet == null)
                continue;

            String name = "";
            Integer health;

            if (pet instanceof Wolf) {
                Wolf wolf = (Wolf) pet;
                name = "オオカミ";
                if (wolf.getCustomName()!=null)
                    name = wolf.getCustomName();
                health = (int) ((Wolf) pet).getHealth();
            } else if (pet instanceof Cat) {
                Cat cat = (Cat) pet;
                name = "ネコ";
                if (cat.getCustomName()!=null)
                    name = cat.getCustomName();
                health = (int) ((Cat) pet).getHealth();
            } else {
                continue;
            }

            sb.append("  ").append(name).append(" (").append(health).append("HP)")
                    .append("\n");
        }

        Util.sendMessage(player, sb.toString());
    }

    @Subcommand("group sync")
    @CommandPermission("grouppets.group.sync")
    private void syncGroup(Player player, @Conditions("groupName") String groupName) {
        PetGroupEntity entity = getPetGroup(player, groupName);
        if (entity == null)
            return;

        entity.getPets().removeIf(Objects::isNull);
        entity.getPets().removeIf(pet -> plugin.getServer().getEntity(pet) == null);
        petGroupRepository.save(entity);
        Util.sendSuccessMessage(player, "グループ " + entity.getName() + " のペットを同期しました。");
    }

    @Subcommand("group sit")
    @CommandPermission("grouppets.group.sit")
    private void sitGroup(Player player, @Conditions("groupName") String groupName) {
        PetGroupEntity entity = getPetGroup(player, groupName);
        if (entity == null)
            return;

        toggleSit(player, entity, true);
        Util.sendSuccessMessage(player, "グループ " + entity.getName() + " のペットを座らせました。");
    }

    @Subcommand("group stand")
    @CommandPermission("grouppets.group.stand")
    private void standGroup(Player player, @Conditions("groupName") String groupName) {
        PetGroupEntity entity = getPetGroup(player, groupName);
        if (entity == null)
            return;

        toggleSit(player, entity, false);
        Util.sendSuccessMessage(player, "グループ " + entity.getName() + " のペットを立たせました。");
    }

    @HelpCommand
    @Default
    private void sendHelp(CommandSender sender) {
        String sb = "Usage: /grouppets <command> [args...]\n" +
                "Commands:\n" +
                "  create group <groupName> - グループを作成します。\n" +
                "  delete group <groupName> - グループを削除します。\n" +
                "  group add <groupName> - ペットをグループに追加します。\n" +
                "  group remove <groupName> - ペットをグループから削除します。\n" +
                "  group sit <groupName> - グループのペットを座らせます。\n" +
                "  group stand <groupName> - グループのペットを立たせます。\n" +
                "  group list - グループの一覧を表示します。\n" +
                "  group list <groupName> - グループの詳細を表示します。\n" +
                "  group sync <groupName> - グループのペットを同期します。";
        Util.sendMessage(sender, sb);
    }

    private PetGroupEntity getPetGroup(Player player, String groupName) {
        PetGroupEntity entity = petGroupRepository.find(groupName, player.getUniqueId());
        if (entity == null) {
            Util.sendErrorMessage(player, "グループ " + groupName + " は存在しません。");
            return null;
        }

        if (!entity.getOwner().equals(player.getUniqueId())) {
            Util.sendErrorMessage(player, "グループ " + groupName + " のオーナーではありません。");
            return null;
        }

        return entity;
    }

    private void toggleSit(Player player, PetGroupEntity entity, boolean isSitting) {
        for (UUID petId : entity.getPets()) {
            Entity pet = plugin.getServer().getEntity(petId);
            if (pet==null)
                continue;

            if (pet instanceof Wolf)
                ((Wolf) pet).setSitting(true);
            else if (pet instanceof Cat)
                ((Cat) pet).setSitting(true);
        }
    }
}
