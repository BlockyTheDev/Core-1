package me.dablakbandit.core.configuration;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandConfiguration extends Configuration{
	
	public CommandConfiguration(JavaPlugin plugin, String filename){
		super(plugin, filename);
	}
	
	public void load(){
		reloadConfig();
		try{
			FileConfiguration config = getConfig();
			boolean save = false;
			for(Field f : this.getClass().getDeclaredFields()){
				if(f.getType().equals(Command.class)){
					String name = WordUtils.capitalize(f.getName().toLowerCase().replaceAll("_", " "));
					if(((Command)f.get(null)).get(config, name, name.replaceAll(" ", ".Argument."))){
						save = true;
					}
				}
			}
			if(save){
				saveConfig();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void reload(){
		load();
	}
	
	public static class Command{
		
		private String		command;
		private String		permission;
		private String[]	aliases;
		private String[]	info;
		
		private String		field;
		
		public Command(String command){
			this(command, null);
		}
		
		public Command(String command, String permission){
			this(command, permission, new String[0]);
		}
		
		public Command(String command, String permission, String[] aliases){
			this(command, permission, aliases, new String[0]);
		}
		
		public Command(String command, String permission, String[] aliases, String[] info){
			this.command = command;
			this.permission = permission;
			this.aliases = aliases;
			this.info = info;
		}
		
		public boolean get(FileConfiguration config, String field, String path){
			boolean save = true;
			this.field = field;
			if(config.isSet(path + ".Command")){
				command = config.getString(path + ".Command");
			}else{
				config.set(path + ".Command", command);
				save = true;
			}
			if(config.isSet(path + ".Permission")){
				permission = config.getString(path + ".Permission");
			}else if(permission != null){
				config.set(path + ".Permission", permission);
				save = true;
			}
			if(config.isSet(path + ".Aliases")){
				aliases = config.getStringList(path + ".Aliases").toArray(new String[0]);
			}else if(aliases.length > 0){
				config.set(path + ".Aliases", Arrays.asList(aliases));
				save = true;
			}
			if(config.isSet(path + ".Info")){
				info = config.getStringList(path + ".Info").toArray(new String[0]);
			}else if(info.length > 0){
				config.set(path + ".Info", Arrays.asList(info));
				save = true;
			}
			return save;
		}
		
		public String getCommand(){
			return command;
		}
		
		public String getPermission(){
			return permission;
		}
		
		public boolean hasPermission(CommandSender s){
			return permission != null && s.hasPermission(permission);
		}
		
		public String[] getAliases(){
			return aliases;
		}
		
		public String[] getInfo(){
			return info;
		}
	}
}
