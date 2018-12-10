/***************************************************************************\
* Copyright 2018 Lance David Selga [Lyonlancer5]                            *
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
package net.lyonlancer5.kawo_extend.modes;

import java.lang.reflect.Method;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityMode_Basic;
import littleMaidMobX.LMM_EnumSound;
import littleMaidMobX.LMM_InventoryLittleMaid;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Rewritten SugarHunter without dependencies
 * 
 * @author Lyonlancer5
 */
public class EntityModeSugarHunter extends LMM_EntityMode_Basic {

	private static final String MODENAME = "SugarHunter";
	private static int MODE_SugarHunter = 0x3201;

	private boolean modeSearchChest = false;
	private int coolTime = 0;
	private double lastdistance = 0;
	private int moveRetryCount = 0;

	private Method mUpdateWanderPath;

	public static void setModeId(int newId) {
		MODE_SugarHunter = newId;
	}

	public EntityModeSugarHunter(LMM_EntityLittleMaid owner) {
		super(owner);
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		EntityAITasks[] ltasks = new EntityAITasks[2];
		ltasks[0] = pDefaultMove;
		ltasks[1] = pDefaultTargeting;
		owner.addMaidMode(ltasks, MODENAME, MODE_SugarHunter);
	}

	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		ItemStack litemstack = owner.maidInventory.getStackInSlot(0);
		if (litemstack != null) {
			if (litemstack.getItem() == Item.getItemFromBlock(Blocks.reeds)) {
				owner.setMaidMode("SugarHunter");
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean checkBlock(int pMode, int px, int py, int pz) {
		if (modeSearchChest) {
			if (owner.maidInventory.getFirstEmptyStack() == -1)
				return super.checkBlock(pMode, px, py, pz);

			else {
				clearMy();
				return false;
			}
		}
		World w = owner.worldObj;
		Block blockId = w.getBlock(px, py, pz);
		if (blockId == Blocks.reeds) {
			if (w.getBlock(px, py - 1, pz) == Blocks.reeds)
				return true;

			if (owner.getRNG().nextInt(600) == 0 && 5 * 5 < owner.getDistanceSq(px + 0.5, py + 0.5, pz + 0.5))
				return true;

		}

		else if (blockId == Blocks.dirt || blockId == Blocks.grass || blockId == Blocks.sand) {
			Material mm = w.getBlock(px, py + 1, pz).getMaterial();
			if (mm == null || (mm.isReplaceable() && !mm.isLiquid()))
				return Blocks.reeds.canPlaceBlockAt(w, px, py + 1, pz)
						&& owner.maidInventory.hasItem(Item.getItemFromBlock(Blocks.reeds));

		}
		return false;
	}

	@Override
	public boolean checkItemStack(ItemStack pItemStack) {
		return true;
	}

	@Override
	public boolean executeBlock(int pMode, int px, int py, int pz) {
		if (modeSearchChest) {
			boolean result = super.executeBlock(pMode, px, py, pz);
			if (!result)
				modeSearchChest = false;

			return result;
		}
		World w = owner.worldObj;
		Block blockId = w.getBlock(px, py, pz);

		if (blockId == Blocks.reeds && w.getBlock(px, py - 1, pz) == Blocks.reeds) {
			owner.setSwing(10, LMM_EnumSound.Null);
			blockId.dropBlockAsItem(w, px, py, pz, 0, 0);
			w.setBlockToAir(px, py, pz);
			w.playSoundEffect(px + 0.5F, py + 0.5F, pz + 0.5F, Blocks.reeds.stepSound.func_150496_b(),
					(Blocks.reeds.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.reeds.stepSound.getPitch() * 0.8F);
			coolTime = 10;
		}

		else if (blockId == Blocks.dirt || blockId == Blocks.grass || blockId == Blocks.sand) {
			Material mm = w.getBlock(px, py + 1, pz).getMaterial();
			if (mm == null || mm.isReplaceable()) {
				if (Blocks.reeds.canPlaceBlockAt(w, px, py + 1, pz)
						&& owner.maidInventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.reeds))) {
					w.setBlock(px, py + 1, pz, Blocks.reeds);
					owner.setSwing(10, LMM_EnumSound.Null);
					w.playSoundEffect(px + 0.5F, py + 0.5F, pz + 0.5F, Blocks.reeds.stepSound.func_150496_b(),
							(Blocks.reeds.stepSound.getVolume() + 1.0F) / 2.0F,
							Blocks.reeds.stepSound.getPitch() * 0.8F);
				}
			}
		}
		return false;
	}

	@Override
	public int getNextEquipItem(int pMode) {
		if (pMode == MODE_SugarHunter) {
			for (int i = 0; i < LMM_InventoryLittleMaid.maxInventorySize; i++) {
				ItemStack item = owner.maidInventory.getStackInSlot(i);
				if (item != null && item.getItem() == Item.getItemFromBlock(Blocks.reeds))
					return i;

			}
		}
		return -1;
	}

	@Override
	public boolean isSearchBlock() {
		if (owner.maidInventory.getFirstEmptyStack() == -1) {
			modeSearchChest = true;
			fDistance = 100F;
			return !super.shouldBlock(mmode_Escorter);
		}
		if (0 < coolTime)
			return false;

		return true; // サトウキビ探索
	}

	@Override
	public void onUpdate(int pMode) {
		super.onUpdate(pMode);
		if (0 < coolTime)
			coolTime--;

	}

	@Override
	public boolean outrangeBlock(int pMode, int pX, int pY, int pZ) {
		if (modeSearchChest)
			return super.outrangeBlock(pMode, pX, pY, pZ);

		boolean result = false;
		if (!owner.isMaidWaitEx()) {
			double distance = owner.getDistanceSq(pX + 0.5, pY + 0.5, pZ + 0.5);
			if (distance == lastdistance) {
				// TODO updateWanderPath() is protected?
				reflect_updateWanderPath();
				result = moveRetryCount < 40;
			} else {
				result = owner.getNavigator().tryMoveToXYZ(pX, pY, pZ, 1.0);
			}
			lastdistance = distance;
		}
		return result;
	}

	@Override
	public int priority() {
		return 5999;
	}

	@Override
	public void resetBlock(int pMode) {
		super.resetBlock(pMode);
		moveRetryCount = 0;
	}

	@Override
	public boolean setMode(int pMode) {
		if (pMode == MODE_SugarHunter) {
			owner.setBloodsuck(false);
			owner.aiWander.setEnable(true);
			owner.aiJumpTo.setEnable(false);
			owner.aiFollow.setEnable(false);
			owner.aiAvoidPlayer.setEnable(false);
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldBlock(int pMode) {
		if (modeSearchChest)
			return super.shouldBlock(pMode);

		return false;
	}

	@Override
	protected void clearMy() {
		super.clearMy();
		modeSearchChest = false;
	}

	private void reflect_updateWanderPath() {

		if (mUpdateWanderPath == null) {
			try {
				mUpdateWanderPath = EntityCreature.class.getDeclaredMethod("updateWanderPath");
			} catch (Exception e) {
				try {
					mUpdateWanderPath = EntityCreature.class.getDeclaredMethod("func_70779_j");

				} catch (Exception e1) {

				}
			}
		}

		if (mUpdateWanderPath != null) {
			try {
				mUpdateWanderPath.invoke(owner);
			} catch (Exception e) {
				//
			}
		}
	}
}
