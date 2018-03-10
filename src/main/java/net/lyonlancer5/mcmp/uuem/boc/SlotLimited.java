package net.lyonlancer5.mcmp.uuem.boc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotLimited extends Slot {

	boolean limited;

	SlotLimited(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		limited = true;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return limited;
	}

}
