# PracticeBot

A Citizens-based Minecraft PvP training bot fork maintained by **THEMPGUY**.

Repository: https://github.com/RealmsNetwork/PracticeBot  
Author: THEMPGUY  
Author URL: https://github.com/THEMPGUYAlt

## Combat

- Normal 1.9+ sword PvP
- Crystal PvP
- Mace PvP on Java 1.21+
- Spear PvP on Java 1.21.11+
- Citizens player NPCs with navigation and server-side movement
- Strafing, sprinting, jumping, knockback recovery and shield handling
- Mace smash planning and Wind Charge-assisted launches
- Density, Breach and Wind Burst
- Spear jab and velocity-based charge behavior
- Spear 2.0 to 4.5 block attack window
- Material-specific Spear timing/damage
- Spear Sharpness and Lunge
- Per-bot weapon profiles saved in the Citizens trait

## Commands

`/spawnbot normal`  
`/spawnbot crystal`  
`/spawnbot mace`  
`/spawnbot spear`

The Spear command checks for a 1.21.11+ Spear material before spawning.

## Configuration

Default weapon kits are in `plugins/PracticeBot/default_inv.yml`.

Modern AI tuning is in `plugins/PracticeBot/config.yml` under `modern-weapons`.

The default Mace kit uses Density V, Wind Burst III and 16 Wind Charges. The default Spear kit uses a Netherite Spear with Sharpness V and Lunge III.

## Build

Requirements:

- Java 21
- Maven 3.9+
- Paper/Purpur 1.21+
- Citizens

Build:

```bash
mvn -B clean test package
```

## GitHub Actions

Pushes and pull requests run the build/test workflow.

Pushing a tag matching `v*.*.*` builds the plugin, uploads the JAR as an Actions artifact, and publishes it to a GitHub Release.

```bash
git tag v1.7.0
git push origin v1.7.0
```

## Compatibility

- Mace: Java 1.21+
- Spear: Java 1.21.11+
- Java: 21
- Citizens: match the server's Citizens build

The Maven build keeps the existing Paper 1.21.1 API dependency. Spear materials are resolved by runtime name so older 1.21.x servers can still load the plugin without a hard compile-time reference to a 1.21.11-only enum constant.

## Implementation notes

The public Bukkit/Citizens API does not expose one call that reproduces every internal vanilla Mace/Spear calculation for a fake player. This fork uses documented vanilla weapon data plus server-side motion/damage handling where the API does not expose the native interaction.
