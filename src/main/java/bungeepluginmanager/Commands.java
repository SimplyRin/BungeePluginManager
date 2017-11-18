package bungeepluginmanager;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.yaml.snakeyaml.Yaml;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

public class Commands extends Command {

	public Commands() {
		super("bungeepluginmanager", null, "bpm" );
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission("bungeepluginmanager.cmds")) {
			sender.sendMessage(BungeePluginManager.getPrefix() + "§cYou don't have access to this command!");
			return;
		}

		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("load")) {
				if(args.length > 1) {
					Plugin plugin = findPlugin(args[1]);
					if (plugin != null) {
						sender.sendMessage(BungeePluginManager.getPrefix() + "§cPlugin is already loaded");
						return;
					}
					File file = findFile(args[1]);
					if (!file.exists()) {
						sender.sendMessage(BungeePluginManager.getPrefix() + "§cPlugin not found");
						return;
					}
					boolean success = PluginUtils.loadPlugin(file);
					if (success) {
						sender.sendMessage(BungeePluginManager.getPrefix() + "§ePlugin loaded");
					} else {
						sender.sendMessage(BungeePluginManager.getPrefix() + "§cFailed to load plugin, see console for more info");
					}
					return;
				}
				sender.sendMessage(BungeePluginManager.getPrefix() + "§cUsage: /bpm load <plugin>");
				return;
			}
			if(args[0].equalsIgnoreCase("unload")) {
				if(args.length > 1) {
					Plugin plugin = findPlugin(args[1]);
					if (plugin == null) {
						sender.sendMessage(BungeePluginManager.getPrefix() + "§cPlugin not found");
						return;
					}
					PluginUtils.unloadPlugin(plugin);
					sender.sendMessage(BungeePluginManager.getPrefix() + "§ePlugin unloaded");
					return;
				}
				sender.sendMessage(BungeePluginManager.getPrefix() + "§cUsage: /bpm unload <plugin>");
				return;
			}
			if(args[0].equalsIgnoreCase("reload")) {
				if(args.length > 1) {
					Plugin plugin = findPlugin(args[1]);
					if (plugin == null) {
						sender.sendMessage(BungeePluginManager.getPrefix() + "§cPlugin not found");
						return;
					}
					File pluginfile = plugin.getFile();
					PluginUtils.unloadPlugin(plugin);
					boolean success = PluginUtils.loadPlugin(pluginfile);
					if (success) {
						sender.sendMessage(BungeePluginManager.getPrefix() + "§ePlugin reloaded");
					} else {
						sender.sendMessage(BungeePluginManager.getPrefix() + "Failed to reload plugin, see console for more info");
					}
					return;
				}
				sender.sendMessage(BungeePluginManager.getPrefix() + "§cUsage: /bpm reload <plugin>");
				return;
			}
		}
		sender.sendMessage(BungeePluginManager.getPrefix() + "§cUsage: /bpm <load|unload|reload> <plugin>");
		// return;
	}

	public static Plugin findPlugin(String pluginname) {
		for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
			if (plugin.getDescription().getName().equalsIgnoreCase(pluginname)) {
				return plugin;
			}
		}
		return null;
	}

	public static File findFile(String pluginname) {
		File folder = ProxyServer.getInstance().getPluginsFolder();
		if (folder.exists()) {
			for (File file : folder.listFiles()) {
				if (file.isFile() && file.getName().endsWith(".jar")) {
					try (JarFile jar = new JarFile(file)) {
						JarEntry pdf = jar.getJarEntry("bungee.yml");
						if (pdf == null) {
							pdf = jar.getJarEntry("plugin.yml");
						}
						try (InputStream in = jar.getInputStream(pdf)) {
							final PluginDescription desc = new Yaml().loadAs(in, PluginDescription.class);
							if (desc.getName().equalsIgnoreCase(pluginname)) {
								return file;
							}
						}
					} catch (Throwable ex) {
					}
				}
			}
		}
		return new File(folder, pluginname + ".jar");
	}

}
