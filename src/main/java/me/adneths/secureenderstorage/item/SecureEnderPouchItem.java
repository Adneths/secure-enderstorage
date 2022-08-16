package me.adneths.secureenderstorage.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.handler.ConfigurationHandler;
import codechicken.enderstorage.item.ItemEnderPouch;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.lib.util.ItemNBTUtils;
import me.adneths.secureenderstorage.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class SecureEnderPouchItem extends ItemEnderPouch
{

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		if (world.isRemote)
		{
			return EnumActionResult.PASS;
		}

		ItemStack stack = player.getHeldItem(hand);
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEnderChest && player.isSneaking())
		{
			TileEnderChest chest = (TileEnderChest) tile;
			ItemNBTUtils.validateTagExists(stack);
			Frequency frequency = chest.frequency.copy();
			if (ConfigurationHandler.anarchyMode && !(frequency.owner != null && frequency.owner.equals(player.getDisplayNameString())))
			{
				frequency.setOwner(null);
			}

			if (!frequency.hasOwner() || frequency.owner.equals(player.getDisplayNameString()) || ConfigurationHandler.anarchyMode)
			{
				frequency.writeToStack(stack);
				return EnumActionResult.SUCCESS;
			} else
				player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[SES] You do not own this ender storage"));
		}
		return EnumActionResult.PASS;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking())
			return new ActionResult(EnumActionResult.PASS, stack);
		if (!world.isRemote)
		{
			Frequency frequency = Frequency.readFromStack(stack);
			if (!frequency.hasOwner() || frequency.owner.equals(player.getDisplayNameString()) || !ModConfig.locked)
				((EnderItemStorage) EnderStorageManager.instance(world.isRemote).getStorage(frequency, "item")).openSMPGui(player,
						stack.getUnlocalizedName() + ".name");
			else
				player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[SES] You do not own this ender storage"));
		}
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}

}
