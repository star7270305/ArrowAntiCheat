package me.arrow.utils.custom;

import com.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import me.arrow.utils.MiscUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import org.bukkit.inventory.ItemStack;

//unused

public class Equipment {

    @Getter
    private ItemStack[] armorContents = new ItemStack[4]; // should be 4, not 3

    @Getter
    private int depthStriderLevel;
    @Getter
    private int frostWalkerLevel;
    @Getter
    private int soulSpeedLevel;
    @Getter
    private int swiftSneakLevel; // NEW

    private int ticks;

    public void handle(Player player) {
        if (this.ticks++ < 5) return;

        this.armorContents = player.getInventory().getArmorContents();

        // Boots
        ItemStack boots = getBoots();
        if (boots != MiscUtils.EMPTY_ITEM) {
            this.depthStriderLevel = boots.getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
            this.frostWalkerLevel = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_13) ? 0 : boots.getEnchantmentLevel(Enchantment.FROST_WALKER);
            this.soulSpeedLevel = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_16) ? 0 : boots.getEnchantmentLevel(Enchantment.SOUL_SPEED);
        }

        // Leggings (Swift Sneak)
        ItemStack leggings = getLeggings();
        if (leggings != MiscUtils.EMPTY_ITEM) {
            this.swiftSneakLevel = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_19) ? 0 : leggings.getEnchantmentLevel(Enchantment.SWIFT_SNEAK);
        }

        this.ticks = 0;
    }

    public ItemStack getBoots() {
        return this.armorContents[0] != null ? this.armorContents[0] : MiscUtils.EMPTY_ITEM;
    }

    public ItemStack getLeggings() {
        return this.armorContents[1] != null ? this.armorContents[1] : MiscUtils.EMPTY_ITEM;
    }

    public ItemStack getChestplate() {
        return this.armorContents[2] != null ? this.armorContents[2] : MiscUtils.EMPTY_ITEM;
    }

    public ItemStack getHelmet() {
        return this.armorContents[3] != null ? this.armorContents[3] : MiscUtils.EMPTY_ITEM;
    }
}
