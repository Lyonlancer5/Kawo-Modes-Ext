/***************************************************************************\
* Copyright 2017 [Lyonlancer5]                                              *
*                                                                           *
* Licensed under the Apache License, Version 2.0 (the "License");           *
* you may not use this file except in compliance with the License.          *
* You may obtain a copy of the License at                                   *
*                                                                           *
*     http://www.apache.org/licenses/LICENSE-2.0                            *
*                                                                           *
* Unless required by applicable law or agreed to in writing, software       *
* distributed under the License is distributed on an "AS IS" BASIS,         *
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
* See the License for the specific language governing permissions and       *
* limitations under the License.                                            *
\***************************************************************************/
package net.lyonlancer5.mcmp.uuem.modes.ac;

import java.util.List;

import com.google.common.collect.Lists;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityModeBase;
import littleMaidMobX.LMM_InventoryLittleMaid;
import net.lyonlancer5.mcmp.uuem.modes.LMMFieldAccessor;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.MathHelper;

/**
 * Recreated Accounter without strategies
 * 
 * @author Lyonlancer5
 */
public class EntityModeAccounter extends LMM_EntityModeBase {

	public static final String MODE_NAME = "Accounter";

	private static int modeID = 0x0202;

	private final IEntitySelector maidSelector;
	private int sugarCount = 64;

	public static void setModeId(int newID) {
		modeID = newID;
	}

	public EntityModeAccounter(LMM_EntityLittleMaid pEntity) {
		super(pEntity);

		maidSelector = new IEntitySelector() {
			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (!(entity instanceof LMM_EntityLittleMaid))
					return false;

				LMM_EntityLittleMaid maid = (LMM_EntityLittleMaid) entity;
				return maid.isContract() && owner.getMaidMasterEntity() == maid.getMaidMasterEntity();
			}
		};
	}

	public int priority() {
		return 7101;
	}

	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		EntityAITasks[] ltasks = new EntityAITasks[] { pDefaultMove, pDefaultTargeting };
		owner.addMaidMode(ltasks, MODE_NAME, modeID);
	}

	@Override
	public boolean checkItemStack(ItemStack pItemStack) {
		return pItemStack != null && pItemStack.getItem() == Items.paper && pItemStack.hasTagCompound()
				&& pItemStack.getTagCompound().getBoolean("lmmxTransactSlip");
	}

	@Override
	public int getNextEquipItem(int pMode) {
		if (pMode == modeID) {
			for (int i = 0; i < LMM_InventoryLittleMaid.maxInventorySize; i++) {
				ItemStack item = owner.maidInventory.getStackInSlot(i);
				if (checkItemStack(item))
					return i;

			}
		}
		return -1;
	}

	public void onUpdate(int pMode) {
		super.onUpdate(pMode);
		if (pMode == modeID) {
			if (owner.worldObj.isRemote)
				return;

			ItemStack transactSlip = owner.maidInventory.getStackInSlot(0);
			// Do not proceed if transaction slip is not present in inventory
			if (!checkItemStack(transactSlip))
				return;

			for (int i = 0; i < LMM_InventoryLittleMaid.maxInventorySize; i++) {
				ItemStack check = owner.maidInventory.getStackInSlot(i);
				if (check != null
						&& (check.getItem() == Items.reeds || check.getItem() == Item.getItemFromBlock(Blocks.reeds))
						&& check.stackSize >= 64) {
					owner.maidInventory.mainInventory[i] = new ItemStack(Items.sugar, check.stackSize);
				}

			}

			List<LMM_EntityLittleMaid> maids = getMaidsInRange();
			NBTTagCompound disp = transactSlip.getTagCompound().getCompoundTag("display");
			NBTTagList lore = new NBTTagList();
			lore.appendTag(new NBTTagString("Sugar Count: " + getSugarAmount(owner)));
			lore.appendTag(new NBTTagString("Maids in range: " + maids.size()));
			disp.setTag("Lore", lore);
			try {
				sugarCount = MathHelper.clamp_int(
						Integer.parseInt(disp.getString("Name").substring("Transaction Slip - ".length())), 0, 81 * 32);
			} catch (Exception e) {
				sugarCount = 128;
			}

			for (LMM_EntityLittleMaid entityLittleMaid : maids) {
				if (getSugarAmount(owner) <= sugarCount)
					break;
				if (entityLittleMaid == owner || !entityLittleMaid.isContractEX())
					continue;

				paySugar(entityLittleMaid);
			}
			boolean nothing = false;
			while (getSugarAmount(owner) < sugarCount && !nothing) {
				nothing = true;
				for (LMM_EntityLittleMaid entityLittleMaid : maids) {
					ItemStack targetSugar = getSugar(entityLittleMaid);
					if (targetSugar == null || entityLittleMaid == owner || !entityLittleMaid.isContractEX())
						continue;
					nothing = false;
					moveSugar(entityLittleMaid, owner, 1);
				}
			}
		}
	}

	public void updateAITick(int pMode) {
		if (pMode == modeID) {
			// TODO
		}
	}

	public boolean setMode(int pMode) {
		if (pMode == modeID) {
			owner.aiAttack.setEnable(false);
			owner.aiShooting.setEnable(false);
			owner.setBloodsuck(false);
			owner.setTracer(false);
			return true;
		}
		return false;
	}

	public boolean changeMode(EntityPlayer pentityplayer) {
		ItemStack p0 = owner.maidInventory.getStackInSlot(0);

		if (p0 != null && p0.getItem() == Items.paper) {
			if (p0.hasTagCompound() && p0.getTagCompound().getBoolean("lmmxTransactSlip"))
				return true;

			NBTTagCompound display = new NBTTagCompound();
			display.setString("Name", "Transaction Slip");
			NBTTagList lore = new NBTTagList();
			lore.appendTag(new NBTTagString("Sugar Count: --"));
			lore.appendTag(new NBTTagString("Maids in range: --"));
			display.setTag("Lore", lore);

			if (p0.getTagCompound() == null)
				p0.setTagCompound(new NBTTagCompound());

			p0.getTagCompound().setTag("display", display);
			p0.getTagCompound().setBoolean("lmmxTransactSlip", true);
			owner.setMaidMode(modeID);
			return true;
		}

		return false;
	}

	private int moveSugar(LMM_EntityLittleMaid srcMaid, LMM_EntityLittleMaid destMaid, int amount) {
		int moveAmount = 0;
		int index = LMMFieldAccessor.getInstance().inventoryContainsItem(srcMaid.maidInventory, Items.sugar);
		if (index == -1)
			return moveAmount;

		ItemStack srcSugar = srcMaid.maidInventory.getStackInSlot(index);
		amount = srcSugar.stackSize > amount ? amount : srcSugar.stackSize;
		ItemStack i = srcSugar.splitStack(amount);
		moveAmount = i.stackSize;
		destMaid.maidInventory.addItemStackToInventory(i);
		if (i.stackSize > 0) {
			moveAmount -= i.stackSize;
			srcMaid.maidInventory.addItemStackToInventory(i);
		}
		if (srcSugar.stackSize <= 0)
			srcMaid.maidInventory.setInventorySlotContents(index, null);

		return moveAmount;
	}

	private void paySugar(LMM_EntityLittleMaid entityLittleMaid) {
		int index = LMMFieldAccessor.getInstance().inventoryContainsItem(owner.maidInventory, Items.sugar);

		int sugarSize = getSugarAmount(entityLittleMaid);
		int paySugarSize = sugarCount - sugarSize;
		if (paySugarSize <= 0)
			return;

		while (index != -1 && paySugarSize > 0) {
			ItemStack sugar = owner.maidInventory.getStackInSlot(index);
			if (sugar.stackSize > paySugarSize) {
				moveSugar(owner, entityLittleMaid, paySugarSize);
				return;
			} else if (sugar.stackSize <= paySugarSize)
				paySugarSize -= moveSugar(owner, entityLittleMaid, sugar.stackSize);

			index = LMMFieldAccessor.getInstance().inventoryContainsItem(owner.maidInventory, Items.sugar);
		}

	}

	private List<LMM_EntityLittleMaid> getMaidsInRange() {
		List<LMM_EntityLittleMaid> list = Lists.newArrayList();
		for (Object o : owner.worldObj.getEntitiesWithinAABBExcludingEntity(owner, owner.boundingBox.expand(16, 16, 16),
				maidSelector)) {
			LMM_EntityLittleMaid lmm = (LMM_EntityLittleMaid) o;
			if (lmm.getMaidMasterEntity() == owner.getMaidMasterEntity())
				list.add(lmm);

		}
		return list;
	}

	private int getSugarAmount(LMM_EntityLittleMaid maid) {
		int count = 0;
		for (ItemStack element : maid.maidInventory.mainInventory)
			if (element != null && element.getItem() == Items.sugar)
				count += element.stackSize;

		return count;
	}

	private ItemStack getSugar(LMM_EntityLittleMaid maid) {
		int i = LMMFieldAccessor.getInstance().inventoryContainsItem(maid.maidInventory, Items.sugar);
		if (i == -1)
			return null;
		return maid.maidInventory.getStackInSlot(i);
	}
}
