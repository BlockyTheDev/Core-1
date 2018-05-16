package me.dablakbandit.core.utils;

import org.bukkit.Material;

public class Version{
	
	public static boolean isAtleastEight(){
		return ItemUtils.getInstance().hasBanner();
	}
	
	private static boolean nine = isNine();
	
	@SuppressWarnings("unused")
	private static boolean isNine(){
		try{
			Material m = Material.ELYTRA;
			return true;
		}catch(Throwable e){
		}
		return false;
	}
	
	public static boolean isAtleastNine(){
		return nine;
	}
}