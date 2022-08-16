package me.adneths.secureenderstorage;


import net.minecraftforge.common.config.Config;

@Config(modid = SecureEnderStorage.MODID)
public class ModConfig {

	@Config.Comment("Whether or not other players can break secure ender storages they don't own (Default: true)")
	public static boolean breakable = true;
	@Config.Comment("Whether or not a secure storage broken by other players should drop obsidian (Default: true)")
	public static boolean obsidian = true;
	@Config.Comment("Whether or not ender storages are inaccessable to players that don't own them (Default: false)")
	public static boolean locked = false;
	
}
