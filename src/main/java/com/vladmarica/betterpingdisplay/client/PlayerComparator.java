package com.vladmarica.betterpingdisplay.client;

import com.google.common.collect.ComparisonChain;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
public class PlayerComparator implements Comparator<PlayerInfo> {

    public int compare(PlayerInfo player1, PlayerInfo player2) {
        PlayerTeam team1 = player1.getTeam();
        PlayerTeam team2 = player2.getTeam();
        return ComparisonChain.start()
                .compareTrueFirst(player1.getGameMode() != GameType.SPECTATOR, player2.getGameMode() != GameType.SPECTATOR)
                .compare(team1 != null ? team1.getName() : "", team2 != null ? team2.getName() : "")
                .compare(player1.getProfile().getName(), player2.getProfile().getName(), String::compareToIgnoreCase)
                .result();
    }
}