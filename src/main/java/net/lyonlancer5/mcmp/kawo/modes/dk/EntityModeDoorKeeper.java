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
package net.lyonlancer5.mcmp.kawo.modes.dk;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityModeBase;
import net.lyonlancer5.mcmp.kawo.modes.StrategyUserHelper;
import net.lyonlancer5.mcmp.kawo.modes.StrategyUserHelperSet;
import net.lyonlancer5.mcmp.unmapi.lib.NonApi;
import net.lyonlancer5.mcmp.unmapi.util.reflect.ReflectionUtils;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;

/**
 * @author Lyonlancer5
 */
public class EntityModeDoorKeeper extends LMM_EntityModeBase {

	//public enum State { TO_OPEN, TO_CLOSE, WAIT; }

	public static final String MODE_NAME = "DoorKeeper";

	private static int modeID = 0x0203;
	static int waitMargin = 60;

	public final StrategyUserHelper<DKDelegate> helper;
	public final StrategyUserHelperSet helpers;

	@NonApi("net.lyonlancer5.mcmp.kawo.LL5_Kawo")
	public static void setModeId(int newID){
		NonApi.Impl.checkAccess(ReflectionUtils.getCaller());
		modeID = newID;
	}
	
	public EntityModeDoorKeeper(LMM_EntityLittleMaid pEntity) {
		super(pEntity);
		helpers = new StrategyUserHelperSet();
		{

			StrategyUserHelper<DoorActivateStrategy> subHelper = new StrategyUserHelper<DoorActivateStrategy>(new DoorCloseStrategy(this));
			subHelper.add(new MasterLookingDoorOpenStrategy(this));
			helper = new StrategyUserHelper<DKDelegate>(new EscorterDKDelegate(this, subHelper));
			subHelper.addDependencyStrategy(helper);
			helpers.add(subHelper);
		}
		{
			StrategyUserHelper<LeverActivateStrategy> subHelper = new StrategyUserHelper<LeverActivateStrategy>(
					new DefaultLeverActivateStrategy(this));
			subHelper.add(new LeverOnStrategy(this));
			subHelper.add(new LeverOffStrategy(this));
			helper.add(new FreedomDKDelegate(this, subHelper));
			subHelper.addDependencyStrategy(helper);
			helpers.add(subHelper);
		}
		helpers.add(helper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		EntityAITasks[] ltasks = new EntityAITasks[2];
		ltasks[0] = new EntityAITasks(owner.aiProfiler);
		ArrayList<EntityAITasks> copyTasks = Lists.newArrayList(pDefaultMove.taskEntries);
		ltasks[0].taskEntries = copyTasks;
		//ltasks[0].removeTask(owner.aiFindBlock);
		//ltasks[0].addTask(4, new EntityAIFindBlockEx(owner));
		ltasks[1] = new EntityAITasks(owner.aiProfiler);

		// 索敵系
		ltasks[1].addTask(1, new EntityAIHurtByTarget(owner, true));

		owner.addMaidMode(ltasks, MODE_NAME, modeID);
	}

	@Override
	public boolean checkBlock(int pMode, int px, int py, int pz) {
		return getCurrentStrategy().checkBlock(pMode, px, py, pz);
	}

	@Override
	public boolean checkItemStack(ItemStack pItemStack) {
		return pItemStack.getItem() == Item.getItemFromBlock(Blocks.lever);
	}

	@Override
	public boolean executeBlock(int pMode, int px, int py, int pz) {
		return getCurrentStrategy().executeBlock(pMode, px, py, pz);
	}

	public DKDelegate getCurrentStrategy() {
		return helper.getCurrentStrategy();
	}

	//@Override
	//public TaskState handleHealthUpdate(LMM_EntityLittleMaid maid, int maidMode, byte par1) {
	//	for (DKDelegate strategy : helper.getStrategies()) {
	//		if (strategy.handleHealthUpdate(maid, maidMode, par1) == TaskState.BREAK) {
	//			return TaskState.BREAK;
	//		}
	//	}
	//	return TaskState.CONTINUE;
	//}

	@Override
	public void init() {
		// 登録モードの名称追加
		//addLocalization(MODE_NAME, new JPNameProvider() {
		//	@Override
		//	public String getLocalization() {
		//		return "門番";
		//	}
		//});
		//LMM_EntityMode_AcceptBookCommand.add(new ModeAlias(MODE_ID, MODE_NAME, "Dk"));
	}

	@Override
	public boolean isSearchBlock() {
		return true;
	}

	@Override
	public void onUpdate(int pMode) {
		if (pMode == modeID) {
			helpers.updateCurrentStrategy();
			helper.getCurrentStrategy().onUpdateStrategy();
		}
	}

	@Override
	public boolean outrangeBlock(int pMode, int pX, int pY, int pZ) {
		return super.outrangeBlock(pMode, pX, pY, pZ);
	}

	@Override
	public int priority() {
		return 7100;
	}

	@Override
	public boolean setMode(int pMode) {
		if (pMode == modeID) {
			owner.setBloodsuck(false);
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldBlock(int pMode) {
		return true;
	}

	@Override
	public void updateAITick(int pMode) {
		if(pMode == modeID) getCurrentStrategy().updateTask(owner, pMode);
	}
	
	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		return checkItemStack(owner.maidInventory.getStackInSlot(0));
	}
}
