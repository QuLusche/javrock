package fr.qulusche.javrock.account;

import fr.qulusche.javrock.Javrock;
import fr.qulusche.javrock.database.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerAccountRepository {

	private final Javrock plugin;
	private final DatabaseManager databaseManager;


	public PlayerAccountRepository(Javrock plugin) {
		this.plugin = plugin;
		this.databaseManager = plugin.getDatabaseManager();
	}

	public void updatePlayerAccount(PlayerAccount playerAccount) {
		if (!checkDatabase()) {
			plugin.getLogger().warning("Database connection is not available. Cannot update player account for " + playerAccount.getUsername());
			return;
		}

		plugin.getFoliaLib().getScheduler().runAsync(task -> {
			try {
				Connection connection = databaseManager.getConnection();

				String sql = "INSERT INTO player_accounts (uuid, username, online, team, created_at, last_updated) VALUES (?, ?, ?, ?, datetime('now'), datetime('now')) "+
						"ON CONFLICT(uuid) DO UPDATE SET username = ?, online = ?, team = ?, last_updated = datetime('now');";

				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setString(1, playerAccount.getUUID().toString());
				statement.setString(2, playerAccount.getUsername());
				statement.setBoolean(3, playerAccount.isOnline());
				statement.setInt(4, playerAccount.getTeam().getPower());
				statement.setString(5, playerAccount.getUsername());
				statement.setBoolean(6, playerAccount.isOnline());
				statement.setInt(7, playerAccount.getTeam().getPower());

				statement.executeUpdate();
				statement.close();

				plugin.getLogger().info("Updated player account for " + playerAccount.getUsername());
			} catch (Exception e) {
				plugin.getLogger().severe("Failed to update player account for " + playerAccount.getUsername() + ": " + e.getMessage());
			}
		});
	}

	public PlayerAccount loadPlayerAccount(UUID uuid) {
		if (!checkDatabase()) {
			plugin.getLogger().warning("Database connection is not available. Cannot load player account.");
			return null;
		}

		CompletableFuture<PlayerAccount> accountFuture = new CompletableFuture<>();

		plugin.getFoliaLib().getScheduler().runAsync(task -> {
			try {
				Connection connection = databaseManager.getConnection();

				String sql = "SELECT * FROM player_accounts WHERE uuid = ?";

				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setString(1, uuid.toString());

				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					accountFuture.complete(new PlayerAccount(
							UUID.fromString(resultSet.getString("uuid")),
							resultSet.getString("username"),
							resultSet.getBoolean("online"),
							PlayerTeam.getTeamFromPower(resultSet.getInt("team"))
					));

					plugin.getLogger().info("Loaded player account for " + uuid);
				} else {
					plugin.getLogger().info("No player account found for UUID " + uuid);
					accountFuture.complete(null);
				}

				resultSet.close();
				statement.close();
			} catch (Exception e) {
				plugin.getLogger().severe("Failed to load player account for UUID " + uuid + ": " + e.getMessage());
				accountFuture.complete(null);
			}
		});

		if (accountFuture.isDone()) return accountFuture.join();
		return null;
	}

	private boolean checkDatabase() {
		if (databaseManager == null) return false;

		try {
			Connection connection = databaseManager.getConnection();
			return connection != null && !connection.isClosed();
		} catch (Exception ignored) {
			return false;
		}
	}
}
