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
package net.lyonlancer5.kawo_extend;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_InventoryLittleMaid;
import net.minecraft.item.Item;

/**
 * Reflection for littleMaidMobX v0.0.9+
 * 
 * @author Lyonlancer5
 */
public class LMMFieldAccessor {

	private static LMMFieldAccessor instance;

	private Field fLMM_EntityLittleMaid_weaponReload;
	private Field fLMM_EntityLittleMaid_weaponFullAuto;

	private Method mLMM_InventoryLittleMaid_getInventorySlotContainItem;

	public static LMMFieldAccessor getInstance() {
		if (instance == null)
			instance = new LMMFieldAccessor();
		return instance;
	}

	private LMMFieldAccessor() {
		try {
			fLMM_EntityLittleMaid_weaponReload = LMM_EntityLittleMaid.class.getDeclaredField("weaponReload");
			fLMM_EntityLittleMaid_weaponReload.setAccessible(true);

			fLMM_EntityLittleMaid_weaponFullAuto = LMM_EntityLittleMaid.class.getDeclaredField("weaponFullAuto");
			fLMM_EntityLittleMaid_weaponFullAuto.setAccessible(true);

			mLMM_InventoryLittleMaid_getInventorySlotContainItem = LMM_InventoryLittleMaid.class
					.getDeclaredMethod("getInventorySlotContainItem", Item.class);
			mLMM_InventoryLittleMaid_getInventorySlotContainItem.setAccessible(true);

		} catch (Exception e) {
			throw new RuntimeException("Backward compatibility hooking FAILED", e);
		}
	}

	/**
	 * <b><i>Backward compatibility API</i></b></br>
	 * Accesses the private field {@code LMM_EntityLittleMaid.weaponReload} and
	 * returns the value contained in the field
	 * 
	 * @param fMaid The entity littleMaid (instance checking)
	 */
	public boolean isWeaponReload(LMM_EntityLittleMaid fMaid) {
		try {
			return fLMM_EntityLittleMaid_weaponReload.getBoolean(fMaid);
		} catch (Exception e) {
			try {
				return fMaid.isWeaponReload();
			} catch (NoSuchMethodError nsme) {
				throw new RuntimeException(
						"Unsupported littleMaidMob API - " + "isWeaponReload() method not found and no access to field "
								+ "LMM_EntityLittleMaid.weaponReload");
			}
		}
	}

	/**
	 * <b><i>Backward compatibility API</i></b></br>
	 * Accesses the private field {@code LMM_EntityLittleMaid.weaponFullAuto} and
	 * returns the value contained in the field
	 * 
	 * @param fMaid The entity littleMaid (instance checking)
	 */
	public boolean isWeaponFullAuto(LMM_EntityLittleMaid fMaid) {
		try {
			return fLMM_EntityLittleMaid_weaponFullAuto.getBoolean(fMaid);
		} catch (Exception e) {
			try {
				return fMaid.isWeaponFullAuto();
			} catch (NoSuchMethodError nsme) {
				throw new RuntimeException("Unsupported littleMaidMob API - "
						+ "isWeaponFullAuto() method not found and no access to field "
						+ "LMM_EntityLittleMaid.weaponFullAuto");
			}
		}
	}

	/**
	 * Checks for an item in the littleMaid inventory (protected method)
	 * 
	 * @param fInv The inventory of a littleMaid to check in
	 * @param itm  The item to find
	 * 
	 * @return The number of the specified item, or -1 if an exception is generated
	 */
	public int inventoryContainsItem(LMM_InventoryLittleMaid fInv, Item itm) {
		try {
			return (Integer) mLMM_InventoryLittleMaid_getInventorySlotContainItem.invoke(fInv, itm);
		} catch (Exception e) {
			return -1;
		}
	}

}
