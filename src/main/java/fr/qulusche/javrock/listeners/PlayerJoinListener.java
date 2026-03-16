package fr.qulusche.javrock.listeners;

import fr.qulusche.javrock.Javrock;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

	private final Javrock plugin;

	public PlayerJoinListener(Javrock plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		plugin.getPlayerAccountManager().getAccount(player)
				.thenAccept(account -> {
					if (account == null) {
						plugin.getFoliaLib().getScheduler().runAtEntity(player, task -> {
							player.kick(Component.text("An error occurred while loading your account. Please try again later."));
						});
					} else {
						plugin.getFoliaLib().getScheduler().runAtEntity(player, task -> {
							player.sendMessage(Component.text("Welcome " + account.getUsername() + "! Your team is " + account.getTeam().name()));
						});
					}
				}).exceptionally(throwable -> {
					plugin.getLogger().severe("Error loading account for " + player.getName() + ": " + throwable.getMessage());
					plugin.getFoliaLib().getScheduler().runAtEntity(player, task ->
							player.kick(Component.text("An error occurred. Please reconnect."))
					);
					return null;
				});
	}
}
