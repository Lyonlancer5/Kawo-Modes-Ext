package net.lyonlancer5.mcmp.uuem.boc;

import net.lyonlancer5.mcmp.uuem.LL5_UUEntityMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemGrimoire extends Item {

	public ItemGrimoire() {
		setMaxStackSize(1);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.block;
	}

	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		return super.onEaten(par1ItemStack, par2World, par3EntityPlayer);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		par3EntityPlayer.setItemInUse(par1ItemStack, getMaxItemUseDuration(par1ItemStack));
		return super.onItemRightClick(par1ItemStack, par2World, par3EntityPlayer);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer,
			int par4) {
		if (par3EntityPlayer.getItemInUseDuration() < 60)
			par3EntityPlayer.openGui(LL5_UUEntityMode.getInstance(), 0, par2World, (int) par3EntityPlayer.posX,
					(int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
		else {
			// アイテムを一定時間使用した
			for (int li = 0; li < 9; li++) {
				// 全カード開放
				ItemStack litemstack = getPrisonCard(par1ItemStack, li);
				if (litemstack != null) {
					Entity lentity = LL5_UUEntityMode.convertCardToEntity(litemstack, par2World);
					if (lentity != null) {
						if (!par2World.isRemote) {
							double ldx = MathHelper.cos((float) (40 * li) + par3EntityPlayer.rotationYawHead);
							double ldz = MathHelper.sin((float) (40 * li) + par3EntityPlayer.rotationYawHead);
							lentity.setPositionAndRotation(par3EntityPlayer.posX + ldx, par3EntityPlayer.posY,
									par3EntityPlayer.posZ + ldz, par3EntityPlayer.rotationYawHead,
									par3EntityPlayer.rotationPitch);
							par2World.spawnEntityInWorld(lentity);
						}
						setPrison(par1ItemStack, li, null);
					}
				}
			}
		}
		super.onPlayerStoppedUsing(par1ItemStack, par2World, par3EntityPlayer, par4);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (entity instanceof EntityLiving && LL5_UUEntityMode.isPrisoner((EntityLiving) entity)) {
			boolean lflag = true;
			for (int li = 0; li < 9; li++) {
				if (getPrison(stack, li) == null) {
					setPrison(stack, li, LL5_UUEntityMode.convertEntityToCard(entity));
					entity.setDead();
					lflag = false;
					LL5_UUEntityMode.showEffect(entity);
					break;
				}
			}
			if (lflag)
				LL5_UUEntityMode.dropConvertedCard(entity);

			return true;
		} else
			return super.onLeftClickEntity(stack, player, entity);

	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		if (par3Entity instanceof EntityPlayer) {
			if (((EntityPlayer) par3Entity).getItemInUse() != par1ItemStack) {
				for (int li = 0; li < 9; li++) {
					ItemStack litemstack = getPrisonCard(par1ItemStack, li);
					if (LL5_UUEntityMode.isSplendor(litemstack)) {
						float lnh = LL5_UUEntityMode.getSplendorHealth(litemstack);
						float lmh = LL5_UUEntityMode.getSplendorMaxHealth(litemstack);
						if (lmh > lnh)
							LL5_UUEntityMode.setSplendorHealth(litemstack, lnh + 0.001F);

					}
				}
			}
		}
	}

	public static NBTTagCompound getPrison(ItemStack pItemStackBook, int pIndex) {
		if (pItemStackBook.hasTagCompound()) {
			String ls = String.format("Prison%02d", pIndex);
			if (pItemStackBook.getTagCompound().hasKey(ls))
				return pItemStackBook.getTagCompound().getCompoundTag(ls);

		}
		return null;
	}

	public static ItemStack getPrisonCard(ItemStack pItemStackBook, int pIndex) {
		NBTTagCompound lnbt = getPrison(pItemStackBook, pIndex);
		if (lnbt != null)
			return ItemStack.loadItemStackFromNBT(lnbt);

		return null;
	}

	public static void setPrison(ItemStack pItemStackBook, int pIndex, ItemStack pItemStackCard) {
		if (!pItemStackBook.hasTagCompound())
			pItemStackBook.setTagCompound(new NBTTagCompound());

		NBTTagCompound ltag = pItemStackBook.getTagCompound();
		String ls = String.format("Prison%02d", pIndex);
		if (pItemStackCard == null)
			ltag.removeTag(ls);
		else {
			NBTTagCompound lnbt = new NBTTagCompound();
			pItemStackCard.writeToNBT(lnbt);
			ltag.setTag(ls, lnbt);
		}
	}

	/**
	 * 中身をインベントリにして返す
	 */
	public static IInventory getInventory(ItemStack pItemStackBook) {
		InventoryBasic linv = new InventoryBasic("Grimoire", false, 9);
		for (int li = 0; li < linv.getSizeInventory(); li++) {
			ItemStack litemstack = getPrisonCard(pItemStackBook, li);
			linv.setInventorySlotContents(li, litemstack);
		}
		return linv;
	}

	public static void setInventory(ItemStack pItemStackBook, IInventory pInventory) {
		for (int li = 0; li < pInventory.getSizeInventory(); li++) {
			ItemStack litemstack = pInventory.getStackInSlot(li);
			setPrison(pItemStackBook, li, litemstack);
		}
	}
}
