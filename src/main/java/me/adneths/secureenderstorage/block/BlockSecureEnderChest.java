package me.adneths.secureenderstorage.block;

import java.util.ArrayList;
import java.util.List;

import codechicken.enderstorage.block.BlockEnderChest;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.util.ItemUtils;
import me.adneths.secureenderstorage.Config;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSecureEnderChest extends BlockEnderChest
{
	public BlockSecureEnderChest(Properties properties)
	{
		super(properties);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		List<ItemStack> drops = new ArrayList<>();
		TileFrequencyOwner tile = (TileFrequencyOwner) builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
		if (tile != null)
		{
			if (!tile.getFrequency().hasOwner() || tile.getFrequency().owner.equals(builder.getOptionalParameter(LootParameters.THIS_ENTITY).getUUID()))
				drops.addAll(super.getDrops(state, builder));
			else if (Config.OBSIDIAN.get())
				drops.add(new ItemStack(Items.OBSIDIAN, 2));
		}
		return drops;
	}

	@SuppressWarnings("deprecation")
	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader reader, BlockPos pos)
	{
		if (!Config.BREAKABLE.get())
		{
			TileFrequencyOwner tile = (TileFrequencyOwner) reader.getBlockEntity(pos);
			if (tile != null)
			{
				if (tile.getFrequency().hasOwner() && !tile.getFrequency().owner.equals(player.getUUID()))
					return 0f;
			}
		}
		return super.getDestroyProgress(state, player, reader, pos);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytrace)
	{
		if (world.isClientSide)
		{
			return ActionResultType.SUCCESS;
		}
		TileEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof TileFrequencyOwner))
		{
			return ActionResultType.FAIL;
		}
		TileFrequencyOwner owner = (TileFrequencyOwner) tile;

		// Normal block trace.
		RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
		if (hit == null)
		{
			return ActionResultType.FAIL;
		}
		if (hit.subHit == 4)
		{
			ItemStack item = player.inventory.getSelected();
			if (player.isCrouching() && owner.getFrequency().hasOwner() && owner.getFrequency().owner.equals(player.getUUID()))
			{
				if (!player.abilities.instabuild && !player.inventory.add(EnderStorageConfig.personalItem.copy()))
				{
					return ActionResultType.FAIL;
				}

				owner.setFreq(owner.getFrequency().copy().setOwner(null));
				return ActionResultType.SUCCESS;
			}
			else if (!item.isEmpty() && ItemUtils.areStacksSameOrTagged(item, EnderStorageConfig.personalItem))
			{
				if (!owner.getFrequency().hasOwner())
				{
					owner.setFreq(owner.getFrequency().copy().setOwner(player.getUUID()).setOwnerName(player.getName()));
					if (!player.abilities.instabuild)
					{
						item.shrink(1);
					}
					return ActionResultType.SUCCESS;
				}
			}
		}
		else if (hit.subHit >= 1 && hit.subHit <= 3)
		{
			if (owner.getFrequency().hasOwner() && !owner.getFrequency().owner.equals(player.getUUID()))
			{
				player.sendMessage(new StringTextComponent(TextFormatting.AQUA + "[SES] You do not own this ender storage"), player.getUUID());
				return ActionResultType.FAIL;
			}
			ItemStack item = player.inventory.getSelected();
			if (!item.isEmpty())
			{
				EnumColour dye = EnumColour.fromDyeStack(item);
				if (dye != null)
				{
					EnumColour[] colours = { null, null, null };
					if (colours[hit.subHit - 1] == dye)
					{
						return ActionResultType.FAIL;
					}
					colours[hit.subHit - 1] = dye;
					owner.setFreq(owner.getFrequency().copy().set(colours));
					if (!player.abilities.instabuild)
					{
						item.shrink(1);
					}
					return ActionResultType.FAIL;
				}
			}
		}
		if (Config.LOCKED.get() && owner.getFrequency().hasOwner() && !owner.getFrequency().owner.equals(player.getUUID()))
		{
			player.sendMessage(new StringTextComponent(TextFormatting.AQUA + "[SES] You do not own this ender storage"), player.getUUID());
			return ActionResultType.FAIL;
		}
		return !player.isCrouching() && owner.activate(player, hit.subHit, hand) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
	}

}
