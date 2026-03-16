package fr.qulusche.javrock.listeners;

import fr.qulusche.javrock.Javrock;
import fr.qulusche.javrock.account.PlayerAccount;
import fr.qulusche.javrock.account.PlayerTeam;
import net.kyori.adventure.text.Component;
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

		PlayerAccount account = plugin.getPlayerAccountManager().getAccount(player);
		if (account == null) {
			plugin.getLogger().warning("PlayerAccount not found for player " + player.getName() + " on quit.");
			return;
		}
		plugin.getPlayerAccountManager().savePlayerAccount(account);

	}
}
