package fr.qulusche.javrock.account;

import fr.qulusche.javrock.Javrock;
import fr.qulusche.javrock.database.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.geysermc.geyser.api.GeyserApi;

import java.util.HashMap;
import java.util.UUID;

public class PlayerAccountManager {

	private final Javrock plugin;

	private final HashMap<UUID, PlayerAccount> playerAccounts;
	private final PlayerAccountRepository playerAccountRepository;

	public PlayerAccountManager(Javrock plugin) {
		this.plugin = plugin;
		this.playerAccounts = new HashMap<>();
		this.playerAccountRepository = new PlayerAccountRepository(plugin);
	}

	public PlayerAccount getAccount(Player player) {
		if (playerAccounts.containsKey(player.getUniqueId())) {
			return playerAccounts.get(player.getUniqueId());
		}

		PlayerAccount playerAccount = playerAccountRepository.loadPlayerAccount(player.getUniqueId());
		if  (playerAccount != null) {
			playerAccounts.put(player.getUniqueId(), playerAccount);
			return playerAccount;
		}

		playerAccount = new PlayerAccount(player.getUniqueId(), player.getName(), true, GeyserApi.api().isBedrockPlayer(player.getUniqueId()) ? PlayerTeam.BEDROCK : PlayerTeam.JAVA);
		playerAccountRepository.updatePlayerAccount(playerAccount);
		playerAccounts.put(player.getUniqueId(), playerAccount);
		return playerAccount;
	}

	public void savePlayerAccount(PlayerAccount playerAccount) {
		playerAccountRepository.updatePlayerAccount(playerAccount);
	}
}
