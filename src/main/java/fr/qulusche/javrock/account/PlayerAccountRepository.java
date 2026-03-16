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
			String sql = "INSERT INTO player_accounts (uuid, username, online, team, created_at, last_updated) " +
					"VALUES (?, ?, ?, ?, datetime('now'), datetime('now')) " +
					"ON CONFLICT(uuid) DO UPDATE SET username = ?, online = ?, team = ?, last_updated = datetime('now')";
			try (Connection connection = databaseManager.getConnection();
				 PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setString(1, playerAccount.getPlayerUUID().toString());
				statement.setString(2, playerAccount.getUsername());
				statement.setBoolean(3, playerAccount.isOnline());
				statement.setInt(4, playerAccount.getTeam().getPower());
				statement.setString(5, playerAccount.getUsername());
				statement.setBoolean(6, playerAccount.isOnline());
				statement.setInt(7, playerAccount.getTeam().getPower());
				statement.executeUpdate();
			} catch (Exception e) {
				plugin.getLogger().severe("Failed to update player account for "
						+ playerAccount.getUsername() + ": " + e.getMessage());
			}
		});

	}

	public CompletableFuture<PlayerAccount> loadPlayerAccount(UUID uuid) {
		CompletableFuture<PlayerAccount> future = new CompletableFuture<>();
		plugin.getFoliaLib().getScheduler().runAsync(task -> {
			try (Connection conn = databaseManager.getConnection();
				 PreparedStatement stmt = conn.prepareStatement(
						 "SELECT * FROM player_accounts WHERE uuid = ?")) {
				stmt.setString(1, uuid.toString());
				ResultSet rs = stmt.executeQuery();
				if (!rs.next()) future.complete(null);
				else future.complete(new PlayerAccount(UUID.fromString(rs.getString("uuid")), rs.getString("username"), rs.getBoolean("online"), PlayerTeam.getTeamFromPower(rs.getInt("team"))));
				rs.close();
			} catch (Exception e) {
				future.completeExceptionally(e);
			}
		});
		return future;
	}
}
