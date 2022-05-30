# idwtialsimmoedm

<!--[![curseforge](https://img.shields.io/badge/-CurseForge-gray?style=for-the-badge&logo=curseforge&labelColor=orange)](https://www.curseforge.com/minecraft/mc-mods/things-fabric)-->
[![modrinth](https://img.shields.io/badge/-modrinth-gray?style=for-the-badge&labelColor=green&labelWidth=15&logo=appveyor&logoColor=white)](https://modrinth.com/mod/idwtialsimmoedm)
[![release](https://img.shields.io/github/v/release/glisco03/things?logo=github&style=for-the-badge)](https://github.com/gliscowo/idwtialsimmoedm/releases)
[![discord](https://img.shields.io/discord/825828008644313089?label=wisp%20forest&logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/xrwHKktV2d)

<center><img src="https://i.imgur.com/lDciZFI.png" alt="tooltip example" width=600></center>

# Overview

idwtialsimmoedm adds descriptions to tooltips that contain enchantments. It uses the same language-key format that [Enchantment Descriptions](https://www.curseforge.com/minecraft/mc-mods/enchantment-descriptions) uses, so all mods that already have descriptions available will be compatible out of the box. idwtialsimmoedm however is completely standalone and very lightweight, yet still comes with a few config options. If you have [Cloth Config](https://modrinth.com/mod/cloth-config) and [Modmenu](https://modrinth.com/mod/modmenu) installed, you will be able to open the config screen otherwise you can edit the file in `config/idwtialsimmoedm.json`.

## Config Options
- **Description Prefix and Indent:** These options determine what kind of bullet-point style to use for displaying the descriptions. If you want to just display the text, leave both of these blank. The indent is used on the second line onwards, if the description is too long and wraps, to align with the first line which uses the prefix
- **Display only when Shift is held:** If this option is enabled, the description tooltip will never show on any item unless the Shift key is held on your keyboard
- **Display only on Enchanted Books:** If this option is enabled, the description tooltip will never be shown on items that aren't Enchanted Books  