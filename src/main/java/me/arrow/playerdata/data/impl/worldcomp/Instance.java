package me.arrow.playerdata.data.impl.worldcomp;

import org.bukkit.Material;
import org.bukkit.World;

public abstract class Instance {
    public abstract Material getType(World world, double x, double y, double z);
}