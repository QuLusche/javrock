package fr.qulusche.javrock;

import com.tcoded.folialib.FoliaLib;
import fr.qulusche.javrock.account.PlayerAccountManager;
import fr.qulusche.javrock.database.DatabaseManager;
import fr.qulusche.javrock.listeners.PlayerJoinListener;
import fr.qulusche.javrock.listeners.PlayerQuitListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Javrock extends JavaPlugin {

	public static JavaPlugin instance;

	@Getter
	private FoliaLib foliaLib;
	@Getter
	private DatabaseManager databaseManager;
	@Getter
	private PlayerAccountManager playerAccountManager;

	@Override
	public void onEnable() {
		instance = this;

		// Initialize FoliaLib
		foliaLib = new FoliaLib(this);

		// Initialize Database
		databaseManager = new DatabaseManager(this);
		databaseManager.connect();

		// Initialize PlayerAccount
		playerAccountManager = new PlayerAccountManager(this);

		// Register Listeners
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

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
