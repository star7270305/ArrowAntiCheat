package me.arrow.utils.custom;

import org.bukkit.potion.PotionEffectType;

// all potions, if any missing, please let me know

public enum PotionType {

    SPEED("SPEED"),
    SLOWNESS("SLOWNESS", "SLOW"),
    HASTE("HASTE", "FAST_DIGGING"),
    MINING_FATIGUE("MINING_FATIGUE", "SLOW_DIGGING"),
    STRENGTH("STRENGTH", "INCREASE_DAMAGE"),
    INSTANT_HEALTH("INSTANT_HEALTH", "HEAL"),
    INSTANT_DAMAGE("INSTANT_DAMAGE", "HARM"),
    JUMP_BOOST("JUMP_BOOST", "JUMP"),
    NAUSEA("NAUSEA", "CONFUSION"),
    REGENERATION("REGENERATION"),
    RESISTANCE("RESISTANCE", "DAMAGE_RESISTANCE"),
    FIRE_RESISTANCE("FIRE_RESISTANCE"),
    WATER_BREATHING("WATER_BREATHING"),
    INVISIBILITY("INVISIBILITY"),
    BLINDNESS("BLINDNESS"),
    NIGHT_VISION("NIGHT_VISION"),
    HUNGER("HUNGER"),
    WEAKNESS("WEAKNESS"),
    POISON("POISON"),
    WITHER("WITHER"),
    HEALTH_BOOST("HEALTH_BOOST"),
    ABSORPTION("ABSORPTION"),
    SATURATION("SATURATION"),

    // 1.9+
    GLOWING("GLOWING"),
    LEVITATION("LEVITATION"),
    LUCK("LUCK"),
    BAD_LUCK("BAD_LUCK", "UNLUCK"),

    // 1.13+
    SLOW_FALLING("SLOW_FALLING"),
    CONDUIT_POWER("CONDUIT_POWER"),
    DOLPHINS_GRACE("DOLPHINS_GRACE"),

    // 1.14+
    BAD_OMEN("BAD_OMEN"),
    HERO_OF_THE_VILLAGE("HERO_OF_THE_VILLAGE"),

    // 1.19+
    DARKNESS("DARKNESS"),

    // newer versions
    TRIAL_OMEN("TRIAL_OMEN"),
    RAID_OMEN("RAID_OMEN"),
    WIND_CHARGED("WIND_CHARGED"),
    WEAVING("WEAVING"),
    OOZING("OOZING"),
    INFESTED("INFESTED"),
    BREATH_OF_THE_NAUTILUS("BREATH_OF_THE_NAUTILUS");

    private final String[] names;

    PotionType(String... names) {
        this.names = names;
    }

    public static boolean isPotionEffect(PotionEffectType type, PotionType potionType) {
        if (type == null || potionType == null) {
            return false;
        }

        String name;

        try {
            name = type.getName();
        } catch (Throwable ignored) {
            return false;
        }

        if (name == null) {
            return false;
        }

        for (String validName : potionType.names) {
            if (validName.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }
}