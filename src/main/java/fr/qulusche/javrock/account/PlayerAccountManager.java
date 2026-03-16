package fr.qulusche.javrock.account;

import fr.qulusche.javrock.Javrock;
import fr.qulusche.javrock.database.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.geysermc.geyser.api.GeyserApi;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerAccountManager {

	private final Javrock plugin;

	private final HashMap<UUID, PlayerAccount> playerAccounts;
	private final PlayerAccountRepository playerAccountRepository;

	public PlayerAccountManager(Javrock plugin) {
		this.plugin = plugin;
		this.playerAccounts = new HashMap<>();
		this.playerAccountRepository = new PlayerAccountRepository(plugin);
	}

	public CompletableFuture<PlayerAccount> getAccount(Player player) {
		if (playerAccounts.containsKey(player.getUniqueId())) {
			return CompletableFuture.completedFuture(
					playerAccounts.get(player.getUniqueId())
			);
		}
		return playerAccountRepository.loadPlayerAccount(player.getUniqueId())
				.thenApply(account -> {
					if (account == null) {
						PlayerAccount newAccount = createAccount(player);
						playerAccounts.put(player.getUniqueId(), newAccount);
						playerAccountRepository.updatePlayerAccount(newAccount);
						return newAccount;
					}
					playerAccounts.put(player.getUniqueId(), account);
					return account;
				});
	}

	public void savePlayerAccount(PlayerAccount playerAccount) {
		playerAccountRepository.updatePlayerAccount(playerAccount);
	}

	public void removePlayerAccount(PlayerAccount playerAccount) {
		playerAccount.setOnline(false);
		savePlayerAccount(playerAccount);
		playerAccounts.remove(playerAccount.getPlayerUUID());
	}

	private PlayerAccount createAccount(Player player) {
		PlayerTeam team = GeyserApi.api().isBedrockPlayer(player.getUniqueId())
				? PlayerTeam.BEDROCK
				: PlayerTeam.JAVA;
		return new PlayerAccount(player.getUniqueId(), player.getName(), true, team);
	}

}
