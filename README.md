# XP Trader

XP Trader is a Forge `1.20.1` mod that adds a villager profession to trade **player XP levels** for emeralds.

## Images
![XP Trader Villager](img/villager.png)
![XP Trader Workstation Recipe](img/recipe.png)

## Requirements
- Minecraft `1.20.1`
- Forge `47.x`
- Java `17`

## What It Adds
- Profession: `xp_trader`
- Workstation: `xp_trader_workstation`
- Vanilla villager trade UI

## Quick Test
```mcfunction
/give @p xp_trader:xp_trader_workstation
/summon minecraft:villager ~ ~ ~ {VillagerData:{type:"minecraft:plains",profession:"xp_trader:xp_trader",level:1},Xp:0}
/experience add @p levels 100
```

## Config
File: `run/config/xp_trader-common.toml`

Main options:
- `levelsPerTradeByLevel`
- `emeraldsPerTradeByLevel`
- `maxUsesPerTradeByLevel`
- `villagerXpPerTradeByLevel`

## Build
```powershell
.\gradlew.bat runClient
.\gradlew.bat build
```
