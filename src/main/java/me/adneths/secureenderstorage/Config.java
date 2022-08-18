package me.adneths.secureenderstorage;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Config
{
	public static final String CATEGORY_GENERAL = "general";

	public static ForgeConfigSpec CONFIG;

	public static ForgeConfigSpec.IntValue FIRSTBLOCK_MAXPOWER;
	public static ForgeConfigSpec.IntValue FIRSTBLOCK_GENERATE;
	public static ForgeConfigSpec.IntValue FIRSTBLOCK_SEND;
	public static ForgeConfigSpec.IntValue FIRSTBLOCK_TICKS;

	public static ForgeConfigSpec.BooleanValue BREAKABLE;
	public static ForgeConfigSpec.BooleanValue OBSIDIAN;
	public static ForgeConfigSpec.BooleanValue LOCKED;

	static
	{
		ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

		SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
		BREAKABLE = SERVER_BUILDER.comment("Whether or not other players can break secure ender storages they don't own (Default: true)")
			.define("breakable", true);
		OBSIDIAN = SERVER_BUILDER.comment("Whether or not a secure storage broken by other players should drop obsidian (Default: true)")
			.define("obsidian", true);
		LOCKED = SERVER_BUILDER.comment("Whether or not ender storages are inaccessable to players that don't own them (Default: false)")
			.define("locked", false);
		SERVER_BUILDER.pop();

		CONFIG = SERVER_BUILDER.build();
	}
}
