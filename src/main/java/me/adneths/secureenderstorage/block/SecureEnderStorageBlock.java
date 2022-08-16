package me.adneths.secureenderstorage.block;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.block.BlockEnderStorage;
import codechicken.enderstorage.handler.ConfigurationHandler;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.util.ItemUtils;
import me.adneths.secureenderstorage.ModConfig;
import me.adneths.secureenderstorage.init.ModContents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SecureEnderStorageBlock extends BlockEnderStorage
{

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);
		if (tile != null)
		{
			if (!tile.frequency.hasOwner() || tile.frequency.owner.equals(this.harvesters.get().getDisplayNameString()))
				super.getDrops(drops, world, pos, state, fortune);
			else if (ModConfig.obsidian)
				drops.add(new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN), 2));
		}
	}

	public static ItemStack createItem(int meta, Frequency freq)
	{
		ItemStack stack = new ItemStack(ModContents.secureEnderStorageBlock, 1, meta);
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		if (ConfigurationHandler.anarchyMode)
			freq.setOwner(null);
		freq.writeToStack(stack);
		return stack;
	}

	@SuppressWarnings("deprecation")
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos)
	{
		if(!ModConfig.breakable)
		{
			TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);
			if (tile != null)
			{
				if(tile.frequency.hasOwner() && !tile.frequency.owner.equals(player.getDisplayNameString()))
					return 0f;
			}
		}
		return super.getPlayerRelativeBlockHardness(state, player, world, pos);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
			float hitZ)
	{
		if (world.isRemote)
		{
			return true;
		}
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileFrequencyOwner))
		{
			return false;
		}
		TileFrequencyOwner owner = (TileFrequencyOwner) tile;

		// Normal block trace.
		RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
		if (hit == null)
		{
			return false;
		}
		if (hit.subHit == 4)
		{
			ItemStack item = player.inventory.getCurrentItem();
			if (player.isSneaking() && owner.frequency.hasOwner() && owner.frequency.owner.equals(player.getDisplayNameString()))
			{
				if (!player.capabilities.isCreativeMode && !player.inventory.addItemStackToInventory(ConfigurationHandler.personalItem.copy()))
				{
					return false;
				}

				owner.setFreq(owner.frequency.copy().setOwner(null));
				return true;
			}
			else if (!item.isEmpty() && ItemUtils.areStacksSameTypeCrafting(item, ConfigurationHandler.personalItem))
			{
				if (!owner.frequency.hasOwner())
				{
					owner.setFreq(owner.frequency.copy().setOwner(player.getDisplayNameString()));
					if (!player.capabilities.isCreativeMode)
					{
						item.shrink(1);
					}
					return true;
				}
			}
		}
		else if (hit.subHit >= 1 && hit.subHit <= 3)
		{
			if (owner.frequency.hasOwner() && !owner.frequency.owner.equals(player.getDisplayNameString()))
			{
				player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[SES] You do not own this ender storage"));
				return false;
			}
			ItemStack item = player.inventory.getCurrentItem();
			if (!item.isEmpty())
			{
				EnumColour dye = EnumColour.fromDyeStack(item);
				if (dye != null)
				{
					EnumColour[] colours = { null, null, null };
					if (colours[hit.subHit - 1] == dye)
					{
						return false;
					}
					colours[hit.subHit - 1] = dye;
					owner.setFreq(owner.frequency.copy().set(colours));
					if (!player.capabilities.isCreativeMode)
					{
						item.shrink(1);
					}
					return true;
				}
			}
		}
		if (ModConfig.locked && owner.frequency.hasOwner() && !owner.frequency.owner.equals(player.getDisplayNameString()))
		{
			player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[SES] You do not own this ender storage"));
			return false;
		}
		return !player.isSneaking() && owner.activate(player, hit.subHit, hand);
	}

}
