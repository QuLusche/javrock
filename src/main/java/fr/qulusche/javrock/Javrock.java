package fr.qulusche.javrock;

import com.tcoded.folialib.FoliaLib;
import fr.qulusche.javrock.database.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Javrock extends JavaPlugin {

	public static JavaPlugin instance;

	private FoliaLib foliaLib;
	private DatabaseManager databaseManager;

	@Override
	public void onEnable() {
		instance = this;

		foliaLib = new FoliaLib(this);

		databaseManager = new DatabaseManager(this);
		databaseManager.connect();

		getLogger().info("Javrock has been enabled!");
	}

	@Override
	public void onDisable() {
		foliaLib.getScheduler().cancelAllTasks();

		databaseManager.disconnect();

		instance = null;
		getLogger().info("Javrock has been disabled!");
	}
}
