package net.lyonlancer5.mcmp.uuem.boc;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotGrimoire extends Slot {

	ItemStack itemstackBook;

	SlotGrimoire(IInventory par1iInventory, int par2, int x, int y) {
		super(par1iInventory, par2, x, y);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return par1ItemStack != null && (par1ItemStack.getItem() instanceof ItemCard);
	}

	@Override
	protected void onCrafting(ItemStack par1ItemStack, int par2) {
		super.onCrafting(par1ItemStack, par2);
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		if (itemstackBook != null)
			ItemGrimoire.setPrison(itemstackBook, slotNumber, getStack());

	}
}
