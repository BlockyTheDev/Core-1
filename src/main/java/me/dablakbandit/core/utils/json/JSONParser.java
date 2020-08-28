package me.dablakbandit.core.utils.json;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.google.gson.*;

import me.dablakbandit.core.utils.NMSUtils;
import me.dablakbandit.core.utils.json.serializer.ItemStackSerializer;
import me.dablakbandit.core.utils.json.serializer.JSONFormatterSerializer;
import me.dablakbandit.core.utils.json.serializer.LocationSerializer;
import me.dablakbandit.core.utils.json.strategy.AnnotationExclusionStrategy;
import me.dablakbandit.core.utils.json.strategy.CorePlayersExclusionStrategy;
import me.dablakbandit.core.utils.json.strategy.Exclude;
import me.dablakbandit.core.utils.jsonformatter.JSONFormatter;

public class JSONParser{
	
	private static Gson			gson;
	private static JsonParser	parser	= new JsonParser();
	
	static{
		GsonBuilder builder = new GsonBuilder();
		builder.serializeNulls();
		builder.registerTypeAdapterFactory(new JSONDataFactory());
		builder.setExclusionStrategies(new AnnotationExclusionStrategy());
		builder.setExclusionStrategies(new CorePlayersExclusionStrategy());
		builder.registerTypeAdapter(ItemStack.class, ItemStackSerializer.getInstance());
		builder.registerTypeAdapter(NMSUtils.getOBCClass("inventory.CraftItemStack"), ItemStackSerializer.getInstance());
		builder.registerTypeAdapter(JSONFormatter.class, new JSONFormatterSerializer());
		builder.registerTypeAdapter(Location.class, new LocationSerializer());
		gson = builder.create();
	}
	
	public static <T> T fromJSON(JsonObject jo, Class<T> clazz){
		return gson.fromJson(jo, clazz);
	}
	
	public static <T> T fromJSON(String json, Class<T> clazz){
		return gson.fromJson(json, clazz);
	}
	
	public static JsonElement parse(String json){
		return parser.parse(json);
	}
	
	public static JsonObject toJson(Object o){
		try{
			return parser.parse(gson.toJson(o)).getAsJsonObject();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void loadAndCopy(Object object, String json){
		Object cloned = fromJSON(json, object.getClass());
		try{
			NMSUtils.getFields(object.getClass()).forEach(field -> {
				if(field.getAnnotation(Exclude.class) != null){ return; }
				if(Modifier.isStatic(field.getModifiers())){ return; }
				if(field.getDeclaringClass().equals(object.getClass())){
					try{
						Object original = field.get(object);
						Object value = field.get(cloned);
						if(original instanceof Collection){
							if(value == null){ return; }
							((Collection)original).clear();
							((Collection)original).addAll((Collection)value);
						}else if(original instanceof Map){
							if(value == null){ return; }
							((Map)original).putAll((Map)value);
						}
						field.set(object, value);
					}catch(Exception e){
						System.err.println(field.getName());
						e.printStackTrace();
					}
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}