package com.vladmarica.betterpingdisplay.client;

import com.google.common.collect.ComparisonChain;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
public class PlayerComparator implements Comparator<NetworkPlayerInfo>
{
    public int compare(NetworkPlayerInfo player1, NetworkPlayerInfo player2)
    {
        ScorePlayerTeam team1 = player1.getPlayerTeam();
        ScorePlayerTeam team2 = player2.getPlayerTeam();
        return ComparisonChain.start()
                .compareTrueFirst(player1.getGameType() != GameType.SPECTATOR, player2.getGameType() != GameType.SPECTATOR)
                .compare(team1 != null ? team1.getName() : "", team2 != null ? team2.getName() : "")
                .compare(player1.getGameProfile().getName(), player2.getGameProfile().getName()).result();
    }
}