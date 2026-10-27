package com.vladmarica.betterpingdisplay.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import com.vladmarica.betterpingdisplay.BetterPingDisplayMod;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderPingHandler {

    @SubscribeEvent
    public void onRenderGuiPre(RenderGameOverlayEvent.Pre event)
    {
        if (event.type == RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            event.setCanceled(true);

            Minecraft mc = Minecraft.getMinecraft();
            ScoreObjective scoreobjective = mc.theWorld.getScoreboard().func_96539_a(0);
            NetHandlerPlayClient handler = mc.thePlayer.sendQueue;
            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            int width = res.getScaledWidth();

            if ((!mc.isIntegratedServerRunning() || handler.playerInfoList.size() > 1 || scoreobjective != null)) {
                mc.mcProfiler.startSection("playerList");
                List<GuiPlayerInfo> players = handler.playerInfoList;
                int maxPlayers = handler.currentServerMaxPlayers;
                int rows = maxPlayers;
                int columns = 1;

                for (columns = 1; rows > 20; rows = (maxPlayers + columns - 1) / columns) {
                    columns++;
                }

                int columnWidth = 300 / columns;

                if (columnWidth > 150) {
                    columnWidth = 150;
                }

                int left = (width - columns * columnWidth) / 2;
                byte border = 10;
                Gui.drawRect(left - 1, border - 1, left + columnWidth * columns, border + 9 * rows, Integer.MIN_VALUE);

                for (int i = 0; i < maxPlayers; i++) {
                    int xPos = left + i % columns * columnWidth;
                    int yPos = border + i / columns * 9;
                    Gui.drawRect(xPos, yPos, xPos + columnWidth - 1, yPos + 8, 553648127);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);

                    if (i < players.size()) {
                        GuiPlayerInfo player = players.get(i);
                        ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(player.name);
                        String displayName = ScorePlayerTeam.formatPlayerName(team, player.name);
                        mc.fontRenderer.drawStringWithShadow(displayName, xPos, yPos, 16777215);

                        int ping = player.responseTime;
                        String pingString = String.format(BetterPingDisplayMod.textFormatString, ping);
                        int strWidth = mc.fontRenderer.getStringWidth(pingString);
                        int pingTextColor = BetterPingDisplayMod.autoColorText ? ColorUtil.getColor(ping) : BetterPingDisplayMod.textColor;
                        mc.fontRenderer.drawStringWithShadow(pingString, xPos + columnWidth - (BetterPingDisplayMod.renderPingBars ? 18 : 2) - strWidth, yPos, pingTextColor);

                        if (scoreobjective != null) {
                            int endX = xPos + mc.fontRenderer.getStringWidth(displayName) + 5;
                            int maxX = xPos + columnWidth - 12 - 5;

                            if (maxX - endX > 5) {
                                Score score = scoreobjective.getScoreboard().func_96529_a(player.name, scoreobjective);
                                String scoreDisplay = EnumChatFormatting.YELLOW + "" + score.getScorePoints();
                                mc.fontRenderer.drawStringWithShadow(scoreDisplay, maxX - mc.fontRenderer.getStringWidth(scoreDisplay), yPos, 16777215);
                            }
                        }
                        
                        if(BetterPingDisplayMod.renderPingBars) {
                        	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                            mc.getTextureManager().bindTexture(Gui.icons);
                            int pingIndex = 4;
                            if (ping < 0) pingIndex = 5;
                            else if (ping < 150) pingIndex = 0;
                            else if (ping < 300) pingIndex = 1;
                            else if (ping < 600) pingIndex = 2;
                            else if (ping < 1000) pingIndex = 3;
                            drawTexturedModalRect(xPos + columnWidth - 12, yPos, 0, 176 + pingIndex * 8, 10, 8, 100);
                        }
                    }
                }
            }
        }
    }
    
    private static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int zLevel)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x), (double)(y + height), zLevel, (double)((float)(u + 0) * f), (double)((float)(v + height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), zLevel, (double)((float)(u + width) * f), (double)((float)(v + height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), zLevel, (double)((float)(u + width) * f), (double)((float)(v + 0) * f1));
        tessellator.addVertexWithUV((double)(x), (double)(y + 0), zLevel, (double)((float)(u + 0) * f), (double)((float)(v + 0) * f1));
        tessellator.draw();
    }
}
