package fr.qulusche.javrock.account;

import lombok.Getter;

import java.util.UUID;

public class PlayerAccount {

	@Getter
	private final UUID UUID;
	@Getter
	private String username;

	@Getter
	private boolean online;

	@Getter
	private PlayerTeam team;

	public PlayerAccount(UUID uuid, String username, boolean online, PlayerTeam team) {
		this.UUID = uuid;
		this.username = username;
		this.online = online;
		this.team = team;
	}
}
