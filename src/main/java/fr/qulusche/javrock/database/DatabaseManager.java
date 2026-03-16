package fr.qulusche.javrock.database;

import fr.qulusche.javrock.Javrock;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseManager {

	private final JavaPlugin plugin;

	@Getter
	private Connection connection;

	public DatabaseManager(Javrock plugin) {
		this.plugin = plugin;
	}

	public void connect() {
		String databaseFile = "database.db";
		File file = new File(plugin.getDataFolder(), databaseFile);
		if  (!file.exists()) {
			try {
				if (file.getParentFile() != null) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				plugin.getLogger().info("Database file created successfully: " + file.getAbsolutePath());
			} catch (Exception e) {
				plugin.getLogger().severe("Failed to create database file: " + e.getMessage());
			}
		}

		try {
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
			plugin.getLogger().info("Connected to SQLite database successfully!");
			plugin.getLogger().info(" └─> Database file: " + databaseFile);
		} catch (SQLException e) {
			plugin.getLogger().severe("Failed to connect to SQLite database: " + e.getMessage());
			throw new RuntimeException(e);
		}

		this.createTables();
	}

	public void disconnect() {
		if (this.connection != null) {
			try {
				this.connection.close();
				plugin.getLogger().info("Database connection closed successfully.");
			} catch (SQLException e) {
				plugin.getLogger().severe("Failed to close database connection: " + e.getMessage());
			}
		}

	}

	private void createTables() {
		try (var statement = this.connection.createStatement()) {

			statement.executeUpdate(
					"CREATE TABLE IF NOT EXISTS player_accounts (" +
							"uuid VARCHAR(36) PRIMARY KEY," +
							"username VARCHAR(16) NOT NULL," +
							"online INTEGER NOT NULL DEFAULT 0," +
							"team INTEGER NOT NULL DEFAULT 0," +
							"created_at TIMESTAMP NOT NULL DEFAULT (datetime('now'))," +
							"last_updated TIMESTAMP NOT NULL DEFAULT (datetime('now'))" +
							")"
			);

			plugin.getLogger().info("Database tables created or verified successfully!");
		} catch (SQLException e) {
			plugin.getLogger().severe("Failed to create database tables: " + e.getMessage());
		}
	}

	public boolean isConnected() {
		try {
			return this.connection != null && !this.connection.isClosed();
		} catch (SQLException e) {
			plugin.getLogger().severe("Failed to check database connection status: " + e.getMessage());
			return false;
		}
	}
}
