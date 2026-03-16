package fr.qulusche.javrock.listeners;

import fr.qulusche.javrock.Javrock;
import fr.qulusche.javrock.account.PlayerAccount;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

	private final Javrock plugin;

	public PlayerQuitListener(Javrock plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		plugin.getPlayerAccountManager().getAccount(player)
				.thenAccept(account -> {
					if (account == null) {
						plugin.getLogger().warning("PlayerAccount not found for player " + player.getName() + " on quit.");
					} else {
						plugin.getPlayerAccountManager().removePlayerAccount(account);
					}
				}).exceptionally(throwable -> {
					plugin.getLogger().severe("Error loading account for " + player.getName() + ": " + throwable.getMessage());
					return null;
				});
	}
}
