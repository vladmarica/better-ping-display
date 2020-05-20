package com.vladmarica.betterpingdisplay.client;

import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.vladmarica.betterpingdisplay.BetterPingDisplayMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderPingHandler {

    private static final Ordering<NetworkPlayerInfo> ENTRY_ORDERING = Ordering.from(new PlayerComparator());

    private static final int PING_TEXT_COLOR = 0xA0A0A0;
    private static final int PING_TEXT_RENDER_OFFSET = -13;
    private static final int PLAYER_SLOT_EXTRA_WIDTH = 45;
    private static final int PLAYER_ICON_WIDTH = 9;

    @SubscribeEvent
    public void onRenderGuiPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            event.setCanceled(true);
            renderPlayerList(Minecraft.getMinecraft());
        }
    }

    /** Copied and modified from {@link net.minecraft.client.gui.GuiPlayerTabOverlay#renderPlayerlist}. */
    private void renderPlayerList(Minecraft mc)
    {
        GuiPlayerTabOverlay playerListGui = mc.ingameGUI.getTabList();
        int width = new ScaledResolution(mc).getScaledWidth();
        Scoreboard scoreboard = mc.world.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(0);

        NetHandlerPlayClient handler = mc.player.connection;
        List<NetworkPlayerInfo> playerList = ENTRY_ORDERING.sortedCopy(handler.getPlayerInfoMap());
        int i = 0;
        int j = 0;

        for (NetworkPlayerInfo player : playerList) {
            int k = mc.fontRenderer.getStringWidth(playerListGui.getPlayerName(player));
            i = Math.max(i, k);

            if (objective != null && objective.getRenderType() != IScoreCriteria.EnumRenderType.HEARTS) {
                k = mc.fontRenderer.getStringWidth(" " + scoreboard.getOrCreateScore(player.getGameProfile().getName(), objective).getScorePoints());
                j = Math.max(j, k);
            }
        }

        playerList = playerList.subList(0, Math.min(playerList.size(), 80));
        int playerCount = playerList.size();
        int i4 = playerCount;
        int j4;

        for (j4 = 1; i4 > 20; i4 = (playerCount + j4 - 1) / j4) {
            ++j4;
        }

        boolean displayPlayerIcons = mc.isIntegratedServerRunning() || mc.getConnection().getNetworkManager().isEncrypted();
        int l;

        if (objective != null) {
            if (objective.getRenderType() == IScoreCriteria.EnumRenderType.HEARTS) {
                l = 90;
            }
            else {
                l = j;
            }
        }
        else {
            l = 0;
        }

        int i1 = Math.min(j4 * ((displayPlayerIcons ? PLAYER_ICON_WIDTH : 0) + i + l + 13 + PLAYER_SLOT_EXTRA_WIDTH), width - 50) / j4;
        int j1 = width / 2 - (i1 * j4 + (j4 - 1) * 5) / 2;
        int k1 = 10;
        int l1 = i1 * j4 + (j4 - 1) * 5;

        List<String> list1 = null;
        if (playerListGui.header != null) {
            list1 = mc.fontRenderer.listFormattedStringToWidth(playerListGui.header.getFormattedText(), width - 50);

            for (String s : list1) {
                l1 = Math.max(l1, mc.fontRenderer.getStringWidth(s));
            }
        }

        List<String> list2 = null;
        if (playerListGui.footer != null) {
            list2 = mc.fontRenderer.listFormattedStringToWidth(playerListGui.footer.getFormattedText(), width - 50);

            for (String s1 : list2) {
                l1 = Math.max(l1, mc.fontRenderer.getStringWidth(s1));
            }
        }

        if (list1 != null) {
            GuiPlayerTabOverlay.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list1.size() * mc.fontRenderer.FONT_HEIGHT, Integer.MIN_VALUE);
            for (String s2 : list1) {
                int i2 = mc.fontRenderer.getStringWidth(s2);
                mc.fontRenderer.drawStringWithShadow(s2, (float)(width / 2 - i2 / 2), (float)k1, -1);
                k1 += mc.fontRenderer.FONT_HEIGHT;
            }
            ++k1;
        }

        GuiPlayerTabOverlay.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + i4 * 9, Integer.MIN_VALUE);

        for (int playerIndex = 0; playerIndex < playerCount; ++playerIndex) {
            int l4 = playerIndex / i4;
            int i5 = playerIndex % i4;
            int j2 = j1 + l4 * i1 + l4 * 5;
            int k2 = k1 + i5 * 9;
            GuiPlayerTabOverlay.drawRect(j2, k2, j2 + i1, k2 + 8, 553648127);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            if (playerIndex < playerList.size()) {
                NetworkPlayerInfo player = playerList.get(playerIndex);
                GameProfile gameprofile = player.getGameProfile();

                if (displayPlayerIcons) {
                    EntityPlayer entityplayer = mc.world.getPlayerEntityByUUID(gameprofile.getId());
                    mc.getTextureManager().bindTexture(player.getLocationSkin());
                    Gui.drawScaledCustomSizeModalRect(j2, k2, 8.0F, 8, 8, 8, 8, 8, 64.0F, 64.0F);

                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        Gui.drawScaledCustomSizeModalRect(j2, k2, 40.0F, 8, 8, 8, 8, 8, 64.0F, 64.0F);
                    }

                    j2 += PLAYER_ICON_WIDTH;
                }

                String s4 = playerListGui.getPlayerName(player);

                if (player.getGameType() == GameType.SPECTATOR) {
                    mc.fontRenderer.drawStringWithShadow(TextFormatting.ITALIC + s4, (float)j2, (float)k2, -1862270977);
                }
                else {
                    mc.fontRenderer.drawStringWithShadow(s4, (float)j2, (float)k2, -1);
                }

                if (objective != null && player.getGameType() != GameType.SPECTATOR) {
                    int k5 = j2 + i + 1;
                    int l5 = k5 + l;

                    if (l5 - k5 > 5)
                    {
                        playerListGui.drawScoreboardValues(objective, k2, gameprofile.getName(), k5, l5, player);
                    }
                }

                // Here is the magic, rendering the ping text
                String pingString = player.getResponseTime() + "ms";
                int pingStringWidth = mc.fontRenderer.getStringWidth(pingString);
                mc.fontRenderer.drawStringWithShadow(
                        pingString,
                        (float) i1 + j2 - pingStringWidth + PING_TEXT_RENDER_OFFSET - (displayPlayerIcons ? PLAYER_ICON_WIDTH : 0),
                        (float) k2, PING_TEXT_COLOR);

                // Render the vanilla ping bars as well
                playerListGui.drawPing(i1, j2 - (displayPlayerIcons ? PLAYER_ICON_WIDTH : 0), k2, player);
            }
        }

        if (list2 != null) {
            k1 = k1 + i4 * 9 + 1;
            GuiPlayerTabOverlay.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list2.size() * mc.fontRenderer.FONT_HEIGHT, Integer.MIN_VALUE);

            for (String s3 : list2)
            {
                int j5 = mc.fontRenderer.getStringWidth(s3);
                mc.fontRenderer.drawStringWithShadow(s3, (float)(width / 2 - j5 / 2), (float)k1, -1);
                k1 += mc.fontRenderer.FONT_HEIGHT;
            }
        }
    }
}
