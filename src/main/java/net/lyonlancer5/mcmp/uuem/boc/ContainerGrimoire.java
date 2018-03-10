package net.lyonlancer5.mcmp.uuem.boc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerGrimoire extends Container {

	private final IInventory inventoryGrimoire;
	private final ItemStack itemstackBook;
	private final int selection;

	public ContainerGrimoire(IInventory pInventoryPlayer, ItemStack pItemStackBook) {
		inventoryGrimoire = ItemGrimoire.getInventory(pItemStackBook);
		inventoryGrimoire.openInventory();
		itemstackBook = pItemStackBook;
		if (pInventoryPlayer instanceof InventoryPlayer) {
			selection = ((InventoryPlayer)pInventoryPlayer).currentItem;
		} else {
			selection = 0;
		}
		
		for (int li = 0; li < (inventoryGrimoire.getSizeInventory() / 3); li++) {
			for (int lj = 0; lj < 3; lj++) {
				SlotGrimoire lslot = new SlotGrimoire(inventoryGrimoire, lj + li * 3, 106 + lj * 18, 22 + li * 18);
				lslot.itemstackBook = itemstackBook;
				this.addSlotToContainer(lslot);
			}
		}
		
		int loffset = 140;
		for (int li = 0; li < 3; li++) {
			for (int lj = 0; lj < 9; lj++) {
				this.addSlotToContainer(new Slot(pInventoryPlayer,
						lj + li * 9 + 9, 8 + lj * 18, li * 18 + loffset));
			}
		}
		for (int li = 0; li < 9; li++) {
			SlotLimited lslot = new SlotLimited(pInventoryPlayer,
					li, 8 + li * 18, 58 + loffset);
			if (li == selection) {
				lslot.limited = false;
			}
			this.addSlotToContainer(lslot);
		}
		
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		inventoryGrimoire.closeInventory();
		ItemGrimoire.setInventory(itemstackBook, inventoryGrimoire);
	}
	
	@Override
	public void onCraftMatrixChanged(IInventory par1iInventory) {
		super.onCraftMatrixChanged(par1iInventory);
		if (par1iInventory == inventoryGrimoire) {
			ItemGrimoire.setInventory(itemstackBook, inventoryGrimoire);
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		ItemStack litemstack = null;
		Slot lslot = getSlot(par2);
		
		if (lslot != null && lslot.getHasStack()) {
			ItemStack litemstack1 = lslot.getStack();
			litemstack = litemstack1.copy();
			
			if (par2 < 9) {
				if (!mergeItemStack(litemstack1, 9, inventorySlots.size(), true)) {
					return null;
				}
				lslot.onSlotChange(litemstack1, litemstack);
			}
			else if (par2 >= 9) {
				for (int li = 0; li < 9; li++) {
					Slot lslot1 = getSlot(li);
					if (!lslot1.getHasStack() && lslot1.isItemValid(litemstack1)) {
						lslot1.putStack(litemstack1.splitStack(1));
						break;
					}
				}
			}
			
			if (litemstack1.stackSize == 0) {
				lslot.putStack((ItemStack) null);
			} else {
				lslot.onSlotChanged();
			}
			
			if (litemstack1.stackSize == litemstack.stackSize) {
				return null;
			}
			
			lslot.onPickupFromSlot(par1EntityPlayer, litemstack1);
		}
		
		return litemstack;
	}

}
