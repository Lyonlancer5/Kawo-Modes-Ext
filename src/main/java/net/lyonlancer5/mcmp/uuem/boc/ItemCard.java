package net.lyonlancer5.mcmp.uuem.boc;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.lyonlancer5.mcmp.uuem.LL5_UUEntityMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemCard extends Item {

	public ItemCard() {
	}

	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		// カードを投げる
		par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!par2World.isRemote) {
			EntityBastilleCard lentity = new EntityBastilleCard(par2World, par3EntityPlayer);
			lentity.setCardItem(par1ItemStack);
			par2World.spawnEntityInWorld(lentity);
		}
		if (!par3EntityPlayer.capabilities.isCreativeMode)
			--par1ItemStack.stackSize;

		return par1ItemStack;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
		if (LL5_UUEntityMode.isSplendor(par1ItemStack)) {
			NBTTagCompound ltag = LL5_UUEntityMode.getSplendorNBT(par1ItemStack);
			par3List.add(ltag.getString("id"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		return LL5_UUEntityMode.isSplendor(par1ItemStack);
	}

	@Override
	public EnumRarity getRarity(ItemStack p_77613_1_) {
		return (LL5_UUEntityMode.isSplendor(p_77613_1_) ? EnumRarity.uncommon : EnumRarity.common);
	}

}
