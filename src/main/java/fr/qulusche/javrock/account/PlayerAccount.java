package fr.qulusche.javrock.account;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class PlayerAccount {

	@Getter
	private final UUID playerUUID;
	@Getter
	private final String username;

	@Getter @Setter
	private boolean online;

	@Getter @Setter
	private PlayerTeam team;

	public PlayerAccount(UUID playerUUID, String username, boolean online, PlayerTeam team) {
		this.playerUUID = playerUUID;
		this.username = username;
		this.online = online;
		this.team = team;
	}

}
