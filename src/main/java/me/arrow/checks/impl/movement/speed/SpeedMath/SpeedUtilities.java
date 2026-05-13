package me.arrow.checks.impl.movement.speed.SpeedMath;

import me.arrow.managers.profile.Profile;
import me.arrow.playerdata.data.impl.VelocityData;
import me.arrow.utils.custom.PotionType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

// our speed utilities, this is where Speed A (Ground) is computed.

public class SpeedUtilities {

    public static final double DEFAULT_WALK_SPEED_ATTRIBUTE = 0.1D;

    public static double computeGroundLimit(Profile profile,
                                            VelocityData velocityData,
                                            double blockFriction,
                                            double iceBoost,
                                            double sprintBase,
                                            double noSprintBase,
                                            double defaultBaseSpeed) {

        double basePerTick = defaultBaseSpeed;

        basePerTick += getGroundSpeedLimitBonus(profile);

        double groundLimit = basePerTick;

        groundLimit += iceBoost;

        if (velocityData != null) {
            groundLimit += velocityData.getTotalHorizontalVelocity();
        }

        return groundLimit;
    }

    public static int getSoulSpeedLevel(Profile profile) {
        try {
            org.bukkit.inventory.ItemStack boots = profile.getPlayer().getInventory().getBoots();

            if (boots == null || boots.getType() == org.bukkit.Material.AIR) {
                return 0;
            }

            org.bukkit.enchantments.Enchantment soulSpeed = org.bukkit.enchantments.Enchantment.getByName("SOUL_SPEED");

            if (soulSpeed == null) {
                return 0;
            }

            return Math.min(3, Math.max(0, boots.getEnchantmentLevel(soulSpeed)));
        } catch (Throwable ignored) {
            return 0;
        }
    }

    public static double getMovementSpeedAttribute(Profile profile) {
        try {
            if (profile == null || profile.getPlayer() == null) {
                return DEFAULT_WALK_SPEED_ATTRIBUTE;
            }

            AttributeInstance attributeInstance = profile.getPlayer().getAttribute(Attribute.MOVEMENT_SPEED);

            if (attributeInstance != null) {
                return Math.max(0.0D, attributeInstance.getBaseValue());
            }
        } catch (Throwable ignored) {
        }

        return DEFAULT_WALK_SPEED_ATTRIBUTE;
    }

    public static double getAttributeBonus(Profile profile, double coefficient, double maxBonus) {
        double attribute = getMovementSpeedAttribute(profile);
        double extra = Math.max(0.0D, attribute - DEFAULT_WALK_SPEED_ATTRIBUTE);

        return Math.min(maxBonus, extra * coefficient);
    }

    public static double getGroundAttributeBonus(Profile profile) {
        return getAttributeBonus(profile, 1.45D, 1.25D);
    }

    public static double getAirAttributeBonus(Profile profile) {
        return getAttributeBonus(profile, 0.75D, 1.15D);
    }

    public static double getGroundPotionBonus(Profile profile) {
        int level = getSpeedPotionLevel(profile);

        if (level <= 0) {
            return 0.0D;
        }

        return Math.min(4.0D, level * 0.034D);
    }

    public static double getAirPotionBonus(Profile profile) {
        int level = getSpeedPotionLevel(profile);

        if (level <= 0) {
            return 0.0D;
        }

        return Math.min(2.25D, level * 0.022D);
    }

    public static int getSpeedPotionLevel(Profile profile) {
        if (profile == null || profile.getPotionData() == null) {
            return 0;
        }

        if (!profile.getPotionData().isHasSpeed()) {
            try {
                if (profile.getMovementData() != null
                        && profile.getMovementData().getSinceSpeedPotionEffectTicks() <= 20) {
                    return Math.max(0, profile.getPotionData().getPotionEffectLevel(PotionType.SPEED));
                }
            } catch (Throwable ignored) {
            }

            return 0;
        }

        return Math.max(0, profile.getPotionData().getPotionEffectLevel(PotionType.SPEED));
    }

    public static double getPotionSpeedMultiplier(Profile profile) {
        int level = getSpeedPotionLevel(profile);

        if (level <= 0) {
            return 1.0D;
        }

        return 1.0D + (0.2D * level);
    }

    public static double getAttributeScale(Profile profile) {
        double attribute = getMovementSpeedAttribute(profile);

        if (attribute <= 0.0D) {
            return 1.0D;
        }

        return Math.max(1.0D, attribute / DEFAULT_WALK_SPEED_ATTRIBUTE);
    }

    public static double getSprintingAttributeSpeed(Profile profile) {
        double attribute = getMovementSpeedAttribute(profile);

        try {
            if (profile != null
                    && profile.getActionData() != null
                    && profile.getActionData().isSprinting()) {
                return attribute * 1.3D;
            }
        } catch (Throwable ignored) {
        }

        return attribute;
    }

    public static double getAttributeAndPotionScale(Profile profile) {
        return getAttributeScale(profile) * getPotionSpeedMultiplier(profile);
    }

    public static double getIceSpeedBoost(double increment, double movingIceTicks, double limit) {
        return movingIceTicks > 0.0D ? Math.min(increment * movingIceTicks, limit) : 0.0D;
    }

    public static double getAfterJumpSpeed(Profile profile) {
        int speedLevel = getSpeedPotionLevel(profile);
        return 0.9175D + (0.008D * speedLevel);
    }

    public static int getPotionEffectLevel(Profile user, PotionEffectType potionEffectType) {
        if (user == null || potionEffectType == null) {
            return 0;
        }

        if (user.getPlayer() == null || !user.getPlayer().isOnline()) {
            return 0;
        }

        try {
            for (PotionEffect effect : user.getPlayer().getActivePotionEffects()) {
                if (effect == null || effect.getType() == null) {
                    continue;
                }

                if (effect.getType().equals(potionEffectType)) {
                    return effect.getAmplifier() + 1;
                }
            }
        } catch (Throwable ignored) {
            return 0;
        }

        return 0;
    }

    public static int getDepthStriderLevel(Profile profile) {
        try {
            if (profile == null || profile.getPlayer() == null) {
                return 0;
            }

            ItemStack boots = profile.getPlayer().getInventory().getBoots();

            if (boots != null && boots.hasItemMeta()) {
                return boots.getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
            }
        } catch (Throwable ignored) {
        }

        return 0;
    }

    public static double getDepthStriderBoost(Profile profile) {
        int depthStriderLevel = getDepthStriderLevel(profile);
        return depthStriderLevel * (2.5 / 20.0D);
    }

    public static double getAttributeExtra(Profile profile) {
        return Math.max(0.0D, getMovementSpeedAttribute(profile) - DEFAULT_WALK_SPEED_ATTRIBUTE);
    }

    public static double getAirAttributePotionBonus(Profile profile) {
        int level = getSpeedPotionLevel(profile);

        if (level <= 0) {
            return 0.0D;
        }

        double extraAttribute = getAttributeExtra(profile);

        if (extraAttribute <= 0.0D) {
            return 0.0D;
        }

        return Math.min(1.25D, extraAttribute * level * 0.42D);
    }

    public static double getGroundAttributePotionBonus(Profile profile) {
        int level = getSpeedPotionLevel(profile);

        if (level <= 0) {
            return 0.0D;
        }

        double extraAttribute = getAttributeExtra(profile);

        if (extraAttribute <= 0.0D) {
            return 0.0D;
        }

        return Math.min(1.75D, extraAttribute * level * 0.55D);
    }

    public static double getAirSpeedLimitBonus(Profile profile) {
        return getAirAttributeBonus(profile)
                + getAirPotionBonus(profile)
                + getAirAttributePotionBonus(profile);
    }

    public static double getGroundSpeedLimitBonus(Profile profile) {
        return getGroundAttributeBonus(profile)
                + getGroundPotionBonus(profile)
                + getGroundAttributePotionBonus(profile);
    }
}