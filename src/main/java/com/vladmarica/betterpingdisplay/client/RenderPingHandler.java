package com.vladmarica.betterpingdisplay.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vladmarica.betterpingdisplay.BetterPingDisplayConfig;
import com.vladmarica.betterpingdisplay.BetterPingDisplayMod;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RenderPingHandler {
    private static final Ordering<NetworkPlayerInfo> ENTRY_ORDERING = Ordering.from(new PlayerComparator());

    private static final int DEFAULT_PING_TEXT_COLOR = 0xA0A0A0;
    private static final String DEFAULT_PING_TEXT_FORMAT = "%dms";
    private static final int PING_TEXT_RENDER_OFFSET = -13;
    private static final int PLAYER_SLOT_EXTRA_WIDTH = 45;
    private static final int PLAYER_ICON_WIDTH = 9;

    private boolean configLoaded = false;
    private int pingTextColor;
    private String pingTextFormat;

    public void loadConfig() {
        if (BetterPingDisplayConfig.textColor.startsWith("#")) {
            try {
                pingTextColor = Integer.parseInt(BetterPingDisplayConfig.textColor.substring(1), 16);
            }
            catch (NumberFormatException ex) {
                BetterPingDisplayMod.logger().error("Config option 'pingTextColor' is invalid - it must be a hex color code");
                pingTextColor = DEFAULT_PING_TEXT_COLOR;
            }
        }
        else {
            pingTextColor = DEFAULT_PING_TEXT_COLOR;
        }

        if (BetterPingDisplayConfig.textFormatString.contains("%d")) {
            pingTextFormat = BetterPingDisplayConfig.textFormatString;
        }
        else {
            pingTextFormat = DEFAULT_PING_TEXT_FORMAT;
            BetterPingDisplayMod.logger().error("Config option 'textFormatString' is invalid - it needs to contain %d");
        }
    }

    @SubscribeEvent
    public void onRenderGuiPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            event.setCanceled(true);

            if (!configLoaded) {
                loadConfig();
                configLoaded = true;
            }

            this.renderPlayerList(event.getMatrixStack(), Minecraft.getInstance());
        }
    }

    /** Copied and modified from {@link PlayerTabOverlayGui#render}.  */
    private void renderPlayerList(MatrixStack matrixStack, Minecraft mc) {
        PlayerTabOverlayGui playerListGui = mc.gui.getTabList();
        int width = mc.getWindow().getGuiScaledWidth();
        Scoreboard scoreboard = mc.level.getScoreboard();
        ScoreObjective objective = scoreboard.getDisplayObjective(0);

        ClientPlayNetHandler handler = mc.player.connection;
        List<NetworkPlayerInfo> playerList = ENTRY_ORDERING.sortedCopy(handler.getOnlinePlayers());
        int i = 0;
        int j = 0;
        Iterator playerIterator = playerList.iterator();

        int nameStringWidth;
        while(playerIterator.hasNext()) {
            NetworkPlayerInfo playerInfo = (NetworkPlayerInfo)playerIterator.next();
            nameStringWidth = mc.font.width(playerListGui.getNameForDisplay(playerInfo));
            i = Math.max(i, nameStringWidth);
            if (objective != null && objective.getRenderType() != ScoreCriteria.RenderType.HEARTS) {
                nameStringWidth = mc.font.width(" " + scoreboard.getOrCreatePlayerScore(playerInfo.getProfile().getName(), objective).getScore());
                j = Math.max(j, nameStringWidth);
            }
        }

        playerList = playerList.subList(0, Math.min(playerList.size(), 80));
        int playerCount = playerList.size();
        int j4 = playerCount;

        int k4;
        for(k4 = 1; j4 > 20; j4 = (playerCount + k4 - 1) / k4) {
            ++k4;
        }

        boolean displayPlayerIcons = mc.isLocalServer() || mc.getConnection().getConnection().isEncrypted();
        int l;
        if (objective != null) {
            if (objective.getRenderType() == ScoreCriteria.RenderType.HEARTS) {
                l = 90;
            } else {
                l = j;
            }
        } else {
            l = 0;
        }

        int i1 = Math.min(k4 * ((displayPlayerIcons ? PLAYER_ICON_WIDTH : 0) + i + l + 13 + PLAYER_SLOT_EXTRA_WIDTH), width - 50) / k4;
        int j1 = width / 2 - (i1 * k4 + (k4 - 1) * 5) / 2;
        int k1 = 10;
        int l1 = i1 * k4 + (k4 - 1) * 5;
        
        List<IReorderingProcessor> list1 = null;
        if (playerListGui.header != null) {
            list1 = mc.font.split(playerListGui.header, width - 50);

            for(IReorderingProcessor s : list1) {
                l1 = Math.max(l1, mc.font.width(s)); /* getStringWidth */
            }
        }

        List<IReorderingProcessor> list2 = null;
        if (playerListGui.footer != null) {
            list2 = mc.font.split(playerListGui.footer, width - 50);

            for(IReorderingProcessor s1 : list2) {
                l1 = Math.max(l1, mc.font.width(s1)); /* getStringWidth */
            }
        }

        if (list1 != null) {
            AbstractGui.fill(matrixStack, width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list1.size() * 9, Integer.MIN_VALUE); /* fill */

            for(IReorderingProcessor s2 : list1) {
                int i2 = mc.font.width(s2); /* getStringWidth */
                mc.font.drawShadow(matrixStack, s2, (float)(width / 2 - i2 / 2), (float)k1, -1); /* drawStringWithShadow */
                k1 += 9;
            }

            ++k1;
        }

        AbstractGui.fill(matrixStack, width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + j4 * 9, Integer.MIN_VALUE); /* fill */
        int l4 = mc.options.getBackgroundColor(553648127);

        for(int playerIndex = 0; playerIndex < playerCount; ++playerIndex) {
            int j5 = playerIndex / j4;
            int j2 = playerIndex % j4;
            int k2 = j1 + j5 * i1 + j5 * 5;
            int l2 = k1 + j2 * 9;
            AbstractGui.fill(matrixStack, k2, l2, k2 + i1, l2 + 8, l4); /* fill */
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (playerIndex < playerList.size()) {
                NetworkPlayerInfo player = playerList.get(playerIndex);
                GameProfile gameprofile = player.getProfile();
                if (displayPlayerIcons) {
                    PlayerEntity playerentity = mc.level.getPlayerByUUID(gameprofile.getId());
                    boolean flag1 = playerentity != null && playerentity.isModelPartShown(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameprofile.getName()) || "Grumm".equals(gameprofile.getName()));
                    mc.getTextureManager().bind(player.getSkinLocation());
                    int i3 = 8 + (flag1 ? 8 : 0);
                    int j3 = 8 * (flag1 ? -1 : 1);
                    AbstractGui.blit(matrixStack, k2, l2, 8, 8, 8.0F, (float)i3, 8, j3, 64, 64);
                    if (playerentity != null && playerentity.isModelPartShown(PlayerModelPart.HAT)) {
                        int k3 = 8 + (flag1 ? 8 : 0);
                        int l3 = 8 * (flag1 ? -1 : 1);
                        AbstractGui.blit(matrixStack, k2, l2, 8, 8, 40.0F, (float)k3, 8, l3, 64, 64);
                    }

                    k2 += PLAYER_ICON_WIDTH;
                }

                ITextComponent displayName = playerListGui.getNameForDisplay(player);
                int nameColor = player.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1;
                mc.font.drawShadow(matrixStack, displayName, (float)k2, (float)l2, nameColor);

                if (objective != null && player.getGameMode() != GameType.SPECTATOR) {
                    int l5 = k2 + i + 1;
                    int i6 = l5 + l;
                    if (i6 - l5 > 5) {
                        playerListGui.renderTablistScore(objective, l2, gameprofile.getName(), l5, i6, player, matrixStack);
                    }
                }

                // Here is the magic, rendering the ping text
                String pingString = String.format(pingTextFormat, player.getLatency());
                int pingStringWidth = mc.font.width(pingString);
                mc.font.drawShadow(
                        matrixStack,
                        new StringTextComponent(pingString),
                        (float) i1 + k2 - pingStringWidth + PING_TEXT_RENDER_OFFSET - (displayPlayerIcons ? PLAYER_ICON_WIDTH : 0),
                        (float) l2,
                        pingTextColor);

                playerListGui.renderPingIcon(matrixStack, i1, k2 - (displayPlayerIcons ? PLAYER_ICON_WIDTH : 0), l2, player); /* drawPing */
            }
        }

        if (list2 != null) {
            k1 = k1 + j4 * 9 + 1;
            AbstractGui.fill(matrixStack, width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list2.size() * 9, Integer.MIN_VALUE);

            for(IReorderingProcessor s3 : list2) {
                int k5 = mc.font.width(s3);
                mc.font.drawShadow(matrixStack, s3, (float)(width / 2 - k5 / 2), (float)k1, -1);
                k1 += 9;
            }
        }
    }
}
