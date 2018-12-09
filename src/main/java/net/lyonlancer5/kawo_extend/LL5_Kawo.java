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
package net.lyonlancer5.kawo_extend;


import java.io.File;
import java.util.Map;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import littleMaidMobX.LMM_EntityModeManager;
import net.lyonlancer5.kawo_extend.modes.ac.EntityModeAccounter;
import net.lyonlancer5.kawo_extend.modes.dk.EntityModeDoorKeeper;
import net.lyonlancer5.kawo_extend.modes.su.EntityModeSugarHunter;
import net.lyonlancer5.mcmp.unmapi.Constants;
import net.lyonlancer5.mcmp.unmapi.util.ModBootstrap;
import net.minecraftforge.common.config.Configuration;

/**
 * Kawo Modes, updated to 1.7.x
 * @author Lyonlancer5
 */
@Mod(modid = LL5_Kawo.MODID, name = "Kawo Modes Extended", version = LL5_Kawo.VERSION, dependencies = "required-after:ll5_unmapi;required-after:lmmx")
public class LL5_Kawo {

	public static final String MODID = "ll5_kawo", VERSION = "1.0.0.0";
	
	private static LL5_Kawo instance;
	
	private ModBootstrap utils;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		utils = ModBootstrap.getInstance(ModBootstrap.DEFAULT_ID, 
				"https://raw.githubusercontent.com/Lyonlancer5/Kawo-Modes/master/hash", 
				"https://raw.githubusercontent.com/Lyonlancer5/Kawo-Modes/master/compat", 
				"https://raw.githubusercontent.com/Lyonlancer5/Kawo-Modes/master/update");
		utils.hashCheck(event.getSourceFile());
		
		Configuration conf = new Configuration(new File(Constants.CONF_DIR, "kawo_modes.cfg"));
		conf.load();
		
		EntityModeAccounter.setModeId(conf.get("Modes", "Accounter ID", 0x0202, "Mode ID for Accounter", 0, Short.MAX_VALUE).getInt());
		EntityModeDoorKeeper.setModeId(conf.get("Modes", "DoorKeeper ID", 0x0203, "Mode ID for Door Keeper", 0, Short.MAX_VALUE).getInt());
		EntityModeSugarHunter.setModeId(conf.get("Modes", "SugarHunter ID", 0x3201, "Mode ID for SugarHunter", 0, Short.MAX_VALUE).getInt());
		
		EntityModeAccounter.setSugarCount(conf.get("Modes", "Accounter Sugar Payment Cutoff", 64, "Determines how much sugar each LittleMaid has if an Accounter is nearby").getInt());
		
		conf.save();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event){
		LMM_EntityModeManager.maidModeList.add(new EntityModeAccounter(null));
        LMM_EntityModeManager.maidModeList.add(new EntityModeDoorKeeper(null));
        LMM_EntityModeManager.maidModeList.add(new EntityModeSugarHunter(null));
        utils.checkUpdate();
	}
	
	@Mod.InstanceFactory
	public static LL5_Kawo getInstance(){
		if(instance == null) instance = new LL5_Kawo();
		return instance;
	}
	
	@NetworkCheckHandler
	public boolean doNetworkCheck(Map<String, String> mods, Side side){
		return mods.containsKey(MODID) && utils.isCompatibleVersion(mods.get(MODID), side);
	}
	
	private LL5_Kawo(){}
	
}
