package fr.qulusche.javrock;

import com.tcoded.folialib.FoliaLib;
import org.bukkit.plugin.java.JavaPlugin;

public final class Javrock extends JavaPlugin {

	public static JavaPlugin instance;

	private static FoliaLib foliaLib;

	@Override
	public void onEnable() {
		instance = this;

		foliaLib = new FoliaLib(this);

		getLogger().info("Javrock has been enabled!");
	}

	@Override
	public void onDisable() {
		foliaLib.getScheduler().cancelAllTasks();

		instance = null;
		getLogger().info("Javrock has been disabled!");
	}
}
