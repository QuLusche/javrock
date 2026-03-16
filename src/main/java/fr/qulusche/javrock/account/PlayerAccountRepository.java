package fr.qulusche.javrock.account;

import fr.qulusche.javrock.Javrock;
import fr.qulusche.javrock.database.DatabaseManager;

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
		if (!databaseManager.isConnected()) {
			plugin.getLogger().warning("Database connection is not available. Cannot update player account for " + playerAccount.getUsername());
			return;
		}

		plugin.getFoliaLib().getScheduler().runAsync(task -> {
			try {
				Connection connection = databaseManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"INSERT INTO player_accounts (uuid, username, online, team, created_at, last_updated) " +
						"VALUES (?, ?, ?, ?, datetime('now'), datetime('now')) " +
						"ON CONFLICT(uuid) DO UPDATE SET username = ?, online = ?, team = ?, last_updated = datetime('now')"
				);

				preparedStatement.setString(1, playerAccount.getPlayerUUID().toString());
				preparedStatement.setString(2, playerAccount.getUsername());
				preparedStatement.setBoolean(3, playerAccount.isOnline());
				preparedStatement.setInt(4, playerAccount.getTeam().getPower());
				preparedStatement.setString(5, playerAccount.getUsername());
				preparedStatement.setBoolean(6, playerAccount.isOnline());
				preparedStatement.setInt(7, playerAccount.getTeam().getPower());
				preparedStatement.executeUpdate();
				preparedStatement.close();
				plugin.getLogger().info("Updated player account for " + playerAccount.getUsername());
			} catch (Exception e) {
				plugin.getLogger().severe("Failed to update player account for "
						+ playerAccount.getUsername() + ": " + e.getMessage());
			}
		});

	}

	public CompletableFuture<PlayerAccount> loadPlayerAccount(UUID uuid) {
		CompletableFuture<PlayerAccount> future = new CompletableFuture<>();

		plugin.getFoliaLib().getScheduler().runAsync(task -> {
			try {
				Connection connection = databaseManager.getConnection();

				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM player_accounts WHERE uuid = ?");
				preparedStatement.setString(1, uuid.toString());

				ResultSet resultSet = preparedStatement.executeQuery();
				if (!resultSet.next()) future.complete(null);
				else future.complete(new PlayerAccount(UUID.fromString(resultSet.getString("uuid")), resultSet.getString("username"), resultSet.getBoolean("online"), PlayerTeam.getTeamFromPower(resultSet.getInt("team"))));

				resultSet.close();
				preparedStatement.close();

			} catch (Exception e) {
				plugin.getLogger().severe("sdsdqsdFailed to load player account for " + uuid.toString());
				future.completeExceptionally(e);
			}
		});
		return future;
	}
}
