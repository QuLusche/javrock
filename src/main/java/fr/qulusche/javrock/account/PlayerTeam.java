package fr.qulusche.javrock.account;

import lombok.Getter;

public enum PlayerTeam {

	MODERATOR(9),
	SPECTATOR(0),
	JAVA(1),
	BEDROCK(2),
	;

	@Getter
	private int power;

	PlayerTeam(int power) {
		this.power = power;
	}

	public static PlayerTeam getTeamFromPower(int power) {
		for (PlayerTeam team : PlayerTeam.values()) {
			if (team.power == power) {
				return team;
			}
		}
		return SPECTATOR;
	}
}
