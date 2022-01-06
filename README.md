# Better Ping Display

[![](http://cf.way2muchnoise.eu/better-ping-display.svg)](https://curseforge.com/minecraft/mc-mods/better-ping-display) [![](http://cf.way2muchnoise.eu/versions/better-ping-display.svg)](https://curseforge.com/minecraft/mc-mods/better-ping-display)

A mod for Minecraft to display each player's ping in the player list as a number. There is also a config file
for changing the text color and format.

![](https://camo.githubusercontent.com/db870df3e5e663d9a7ca5e6dafc0d72c68fd31d909ab623378a778ee9289719c/68747470733a2f2f692e696d6775722e636f6d2f485472483069322e706e67)

This is a client-side mod. The server doesn't need to have it installed. It works even when playing on vanilla servers.

## Configuration
This mod's config file is `betterpingdisplay-client.toml`. It contains the following options:

| Option            | Default Value  | Description  |
|-------------------|---|---|
| autoColorText | `true` | Whether to color a player's ping based on their latency. E.g, low latency = green, high latency = red |
| renderPingBars    | `false` | Whether to also draw the default Minecraft ping bars  |
| textColor         | `#A0A0A0`  | The ping text color to use. Only works whens `autoColorText` is false |
| textFormatString  | `%dms` | The format string for ping text. Must include a `%d`, which will be replaced dynamically by the actual ping value.

## Requirements
* [Minecraft Forge](http://files.minecraftforge.net)
