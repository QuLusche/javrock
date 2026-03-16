package fr.qulusche.javrock.listeners;

import fr.qulusche.javrock.Javrock;
import fr.qulusche.javrock.account.PlayerAccount;
import fr.qulusche.javrock.account.PlayerTeam;
import fr.qulusche.javrock.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.geysermc.geyser.api.GeyserApi;

public class PlayerJoinListener implements Listener {

	private Javrock plugin;

	public PlayerJoinListener(Javrock plugin) {
		this.plugin = plugin;
	}

	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		PlayerAccount account = plugin.getPlayerAccountManager().getAccount(player);
		if (account == null) {
			player.kick(Component.text("JavRock - An error occurred while loading your account. Please try again later."));
		}

		if (account.getTeam() == PlayerTeam.JAVA) {
			player.sendMessage("Bienvenue sur JavRock, " + player.getName() + "! Vous êtes dans l'équipe Java.");
		} else if (account.getTeam() == PlayerTeam.BEDROCK) {
			player.sendMessage("Bienvenue sur JavRock, " + player.getName() + "! Vous êtes dans l'équipe Bedrock.");
		} else {
			player.sendMessage("Bienvenue sur JavRock, " + player.getName() + "!");
		}
	}
}
