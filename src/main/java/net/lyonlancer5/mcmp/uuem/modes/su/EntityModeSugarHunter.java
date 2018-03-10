/***************************************************************************\
* Copyright 2013 Yamato	                                                    *
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
package net.lyonlancer5.mcmp.uuem.modes.su;

import java.util.Random;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityMode_Basic;
import littleMaidMobX.LMM_EnumSound;
import littleMaidMobX.LMM_InventoryLittleMaid;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Waco_EntityModeSugarHunter, updated to 1.7
 * 
 * @author Yamato 
 */
public class EntityModeSugarHunter extends LMM_EntityMode_Basic {
	
	private static int mmode_SugarHunter = 0x3201;
	private final Random rand = new Random();
	private boolean modeSearchChest = false;
	private int coolTime = 0;
	
	public EntityModeSugarHunter(LMM_EntityLittleMaid owner) {
		super(owner);
	}

	public static void setModeId(int newID) {
		mmode_SugarHunter = newID;
	}
	
	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		EntityAITasks[] ltasks = new EntityAITasks[2];
		ltasks[0] = pDefaultMove;
		ltasks[1] = pDefaultTargeting;
		owner.addMaidMode(ltasks, "SugarHunter", mmode_SugarHunter);
	}
	
	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		ItemStack litemstack = owner.maidInventory.getStackInSlot(0);
		if (litemstack != null) {
			if (litemstack.getItem() == Items.reeds) {
				owner.setMaidMode(mmode_SugarHunter);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean checkBlock(int pMode, int px, int py, int pz) {
		if (modeSearchChest) {
			if (isFullInventory()) {
				return super.checkBlock(pMode, px, py, pz);
			}
			else {
				clearMy();
				return false;
			}
		}
		// サトウキビの探索
		World w = owner.worldObj;
		Block block = w.getBlock(px, py, pz);
		if (block == Blocks.reeds) {
			if (w.getBlock(px, py - 1, pz) == Blocks.reeds) {
				return true;
			}
			// 伸びていなければ離れた場所に移動してみる
			if (rand.nextInt(600) == 0 && 5 * 5 < owner.getDistanceSq(px + 0.5, py + 0.5, pz + 0.5)) {
				return true;
			}
		}
		// 植え付け可能場所の探索
		else if (block == Blocks.dirt || block == Blocks.grass || block == Blocks.sand) {
			Material mm = w.getBlock(px, py + 1, pz).getMaterial();
			if (mm == null || (mm.isReplaceable() && !mm.isLiquid())) {
				if (Blocks.reeds.canPlaceBlockAt(w, px, py + 1, pz) && owner.maidInventory.hasItem(Items.reeds)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean checkItemStack(ItemStack pItemStack) {
		return true; // インベントリに空きがあればアイテムは拾いに行く。けど AICollectItem 内でも判定しているみたい
	}
	
	@Override
	public boolean executeBlock(int pMode, int px, int py, int pz) {
		if (modeSearchChest) {
			boolean result = super.executeBlock(pMode, px, py, pz);
			if (!result) {
				modeSearchChest = false;
			}
			return result;
		}
		World w = owner.worldObj;
		Block block = w.getBlock(px, py, pz);
		
		// さとうきびの刈り取り
		if (block == Blocks.reeds && w.getBlock(px, py - 1, pz) == Blocks.reeds) {
			owner.setSwing(10, LMM_EnumSound.Null);
			block.dropBlockAsItem(w, px, py, pz, 0, 0);
			w.setBlockToAir(px, py, pz);
			w.playSoundEffect(px + 0.5F, py + 0.5F, pz + 0.5F, Blocks.reeds.stepSound.getBreakSound(), (Blocks.reeds.stepSound.getVolume() + 1.0F) / 2.0F,
					Blocks.reeds.stepSound.getPitch() * 0.8F);
			coolTime = 10;
		}
		// 植え付け
		else if (block == Blocks.dirt || block == Blocks.grass || block == Blocks.sand) {
			Material mm = w.getBlock(px, py + 1, pz).getMaterial();
			if (mm == null || mm.isReplaceable()) {
				if (Blocks.reeds.canPlaceBlockAt(w, px, py + 1, pz) && owner.maidInventory.consumeInventoryItem(Items.reeds)) {
					w.setBlock(px, py + 1, pz, Blocks.reeds);
					owner.setSwing(10, LMM_EnumSound.Null);
					w.playSoundEffect(px + 0.5F, py + 0.5F, pz + 0.5F, Blocks.reeds.stepSound.getBreakSound(),
							(Blocks.reeds.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.reeds.stepSound.getPitch() * 0.8F);
				}
			}
		}
		return false;
	}
	
	@Override
	public int getNextEquipItem(int pMode) {
		if (pMode == mmode_SugarHunter) {
			for (int i = 0; i < LMM_InventoryLittleMaid.maxInventorySize; i++) {
				ItemStack item = owner.maidInventory.getStackInSlot(i);
				if (item != null && item.getItem() == Items.reeds) {
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
	public void init() {
		;
	}
	
	@Override
	public boolean isSearchBlock() {
		if (isFullInventory()) {
			owner.aiWander.setEnable(true); // チェスト探索時には徘徊を許可する
			modeSearchChest = true; // アイテムいっぱいならチェスト探索モード
			fDistance = 100F; // チェストまでの最大距離
			return !super.shouldBlock(mmode_Escorter); // ほんとは mytile == null したいけど代用として
		}
		if (0 < coolTime) {
			return false; // クールタイム中は立ち止まる
		}
		return true; // サトウキビ探索
	}
	
	@Override
	public void onUpdate(int pMode) {
		super.onUpdate(pMode);
		if (0 < coolTime) {
			coolTime--;
		}
	}
	
	@Override
	public boolean outrangeBlock(int pMode, int pX, int pY, int pZ) {
		if (modeSearchChest) {
			return super.outrangeBlock(pMode, pX, pY, pZ);
		}
		boolean result = false;
		if (!owner.isMaidWaitEx()) {
			result = owner.getNavigator().tryMoveToXYZ(pX, pY, pZ, 1.0);
		}
		return result;
	}
	
	@Override
	public int priority() {
		return 5999; // IC2だとサトウキビが燃えるようになるので、LMM_EntityMode_Cooking より優先度を上げる
	}
	
	@Override
	public boolean setMode(int pMode) {
		if (pMode == mmode_SugarHunter) {
			owner.setBloodsuck(false);
			owner.aiWander.setEnable(false); // デフォルト false
			owner.aiJumpTo.setEnable(false);
			owner.aiFollow.setEnable(false);
			owner.aiAvoidPlayer.setEnable(false);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean shouldBlock(int pMode) {
		if (modeSearchChest) {
			return super.shouldBlock(pMode);
		}
		return false;
	}
	
	private boolean isFullInventory() {
		return owner.maidInventory.getFirstEmptyStack() == -1;
	}
	
	@Override
	protected void clearMy() {
		super.clearMy();
		modeSearchChest = false; // リセット
		owner.aiWander.setEnable(false);
	}
}

