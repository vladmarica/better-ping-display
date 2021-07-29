package com.vladmarica.betterpingdisplay.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vladmarica.betterpingdisplay.BetterPingDisplayConfig;
import com.vladmarica.betterpingdisplay.BetterPingDisplayMod;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RenderPingHandler {
    private static final Ordering<PlayerInfo> ENTRY_ORDERING = Ordering.from(new PlayerComparator());

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

    /** Copied and modified from {@link net.minecraft.client.gui.components.PlayerTabOverlay#render}.  */
    private void renderPlayerList(PoseStack matrixStack, Minecraft mc) {
        PlayerTabOverlay playerListGui = mc.gui.getTabList();
        int width = mc.getWindow().getGuiScaledWidth();
        Scoreboard scoreboard = mc.level.getScoreboard();
        Objective objective = scoreboard.getDisplayObjective(0);

        ClientPacketListener handler = mc.player.connection;
        List<PlayerInfo> playerList = ENTRY_ORDERING.sortedCopy(handler.getOnlinePlayers());
        int i = 0;
        int j = 0;
        Iterator playerIterator = playerList.iterator();

        int nameStringWidth;
        while(playerIterator.hasNext()) {
            PlayerInfo playerInfo = (PlayerInfo) playerIterator.next();
            nameStringWidth = mc.font.width(playerListGui.getNameForDisplay(playerInfo));
            i = Math.max(i, nameStringWidth);
            if (objective != null && objective.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
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
            if (objective.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
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
        
        List<FormattedCharSequence> list1 = null;
        if (playerListGui.header != null) {
            list1 = mc.font.split(playerListGui.header, width - 50);

            for(FormattedCharSequence s : list1) {
                l1 = Math.max(l1, mc.font.width(s)); /* getStringWidth */
            }
        }

        List<FormattedCharSequence> list2 = null;
        if (playerListGui.footer != null) {
            list2 = mc.font.split(playerListGui.footer, width - 50);

            for(FormattedCharSequence s1 : list2) {
                l1 = Math.max(l1, mc.font.width(s1)); /* getStringWidth */
            }
        }

        if (list1 != null) {
            GuiComponent.fill(matrixStack, width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list1.size() * 9, Integer.MIN_VALUE); /* fill */

            for(FormattedCharSequence s2 : list1) {
                int i2 = mc.font.width(s2); /* getStringWidth */
                mc.font.drawShadow(matrixStack, s2, (float)(width / 2 - i2 / 2), (float)k1, -1); /* drawStringWithShadow */
                k1 += 9;
            }

            ++k1;
        }

        GuiComponent.fill(matrixStack, width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + j4 * 9, Integer.MIN_VALUE); /* fill */
        int l4 = mc.options.getBackgroundColor(553648127);

        for(int playerIndex = 0; playerIndex < playerCount; ++playerIndex) {
            int j5 = playerIndex / j4;
            int j2 = playerIndex % j4;
            int k2 = j1 + j5 * i1 + j5 * 5;
            int l2 = k1 + j2 * 9;
            GuiComponent.fill(matrixStack, k2, l2, k2 + i1, l2 + 8, l4); /* fill */
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (playerIndex < playerList.size()) {
                PlayerInfo player = playerList.get(playerIndex);
                GameProfile gameprofile = player.getProfile();
                if (displayPlayerIcons) {
                    Player playerentity = mc.level.getPlayerByUUID(gameprofile.getId());
                    boolean flag1 = playerentity != null && playerentity.isModelPartShown(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameprofile.getName()) || "Grumm".equals(gameprofile.getName()));
                    RenderSystem.setShaderTexture(0, player.getSkinLocation());
                    int i3 = 8 + (flag1 ? 8 : 0);
                    int j3 = 8 * (flag1 ? -1 : 1);
                    GuiComponent.blit(matrixStack, k2, l2, 8, 8, 8.0F, (float)i3, 8, j3, 64, 64);
                    if (playerentity != null && playerentity.isModelPartShown(PlayerModelPart.HAT)) {
                        int k3 = 8 + (flag1 ? 8 : 0);
                        int l3 = 8 * (flag1 ? -1 : 1);
                        GuiComponent.blit(matrixStack, k2, l2, 8, 8, 40.0F, (float)k3, 8, l3, 64, 64);
                    }

                    k2 += PLAYER_ICON_WIDTH;
                }

                Component displayName = playerListGui.getNameForDisplay(player);
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
                        new TextComponent(pingString),
                        (float) i1 + k2 - pingStringWidth + PING_TEXT_RENDER_OFFSET - (displayPlayerIcons ? PLAYER_ICON_WIDTH : 0),
                        (float) l2,
                        pingTextColor);

                playerListGui.renderPingIcon(matrixStack, i1, k2 - (displayPlayerIcons ? PLAYER_ICON_WIDTH : 0), l2, player); /* drawPing */
            }
        }

        if (list2 != null) {
            k1 = k1 + j4 * 9 + 1;
            GuiComponent.fill(matrixStack, width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list2.size() * 9, Integer.MIN_VALUE);

            for(FormattedCharSequence s3 : list2) {
                int k5 = mc.font.width(s3);
                mc.font.drawShadow(matrixStack, s3, (float)(width / 2 - k5 / 2), (float)k1, -1);
                k1 += 9;
            }
        }
    }
}
