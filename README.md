# PracticeBot

A Citizens-based Minecraft PvP training bot fork maintained by **THEMPGUY**.

Repository: https://github.com/RealmsNetwork/PracticeBot  
Author: THEMPGUY  
Author URL: https://github.com/THEMPGUYAlt

## Modern combat

This fork keeps the existing Citizens player-NPC architecture and adds a dedicated modern weapon combat layer.

- Normal 1.9+ sword PvP
- Crystal PvP
- Mace PvP for Java 1.21+
- Spear PvP for Java 1.21.11+
- Citizens player NPCs with navigation and server-side movement
- Mace smash fall-distance damage and launch planning
- Density, Breach and Wind Burst handling
- Wind Charge-assisted Mace launches
- Spear Jab and kinetic Charge behavior
- Spear minimum reach and extended reach
- Spear Lunge
- Shield-aware attack decisions
- Knockback, sprinting and movement state carried through Citizens traits
- Per-bot weapon profile persistence

Minecraft Java 1.21.11 describes the Spear as a two-attack weapon with Jab and Charge. Charge is a kinetic attack whose damage depends on relative velocity, view direction and weapon data. The same release also adds custom attack ranges and the kinetic weapon data component. See the Minecraft Java Edition 1.21.11 release notes for the complete technical specification.

Minecraft's Mace smash uses fall distance with a falloff of 4 damage per block for the first 3 blocks, 2 for the next 5, then 1 per block beyond 8. Density adds 0.5 damage per fallen block per level, while Breach reduces armor effectiveness by 15% per level. Wind Burst launches the attacker upward after a smash. These values are taken from Mojang's published Java combat snapshots.

## Commands

`/spawnbot normal`  
`/spawnbot crystal`  
`/spawnbot mace`  
`/spawnbot spear`

Spear spawning checks for the runtime Spear material, so the plugin still loads on older 1.21.x servers that do not yet expose 1.21.11 Spear materials.

## Configuration

Default weapon kits are in `plugins/PracticeBot/default_inv.yml`.

Modern AI tuning is in `plugins/PracticeBot/config.yml` under `modern-weapons`.

The default Mace kit uses Density V, Wind Burst III and 16 Wind Charges. The default Spear kit uses a Netherite Spear with Sharpness V and Lunge III.

The Spear charge velocity multiplier is configurable because the public release notes document the kinetic calculation model but do not expose every per-material item-component constant in Bukkit's older 1.21.1 compile API. The bot therefore uses the documented kinetic formula while keeping that multiplier server-configurable.

## Build

Requirements:

- Java 21
- Maven 3.9+
- Paper/Purpur 1.21+
- Citizens

Build locally:

```bash
mvn -B clean test package
```

The JAR is generated under `target/`.

## GitHub Actions

Every push and pull request runs the build/test workflow and stores the resulting plugin JAR as an Actions artifact.

Pushing a tag matching `v*.*.*` runs the release workflow. It rebuilds and tests the plugin, uploads the JAR as an artifact, and publishes the JAR to a GitHub Release.

Example:

```bash
git tag v1.7.0
git push origin v1.7.0
```

## Compatibility

- Mace: Java 1.21+
- Spear: Java 1.21.11+
- Java: 21
- Citizens: use the Citizens build compatible with your server

The Maven build still targets the existing Paper 1.21.1 API. Spear materials are resolved by runtime name rather than a compile-time 1.21.11 enum reference.

## Notes

Bukkit/Citizens does not provide one public API call that reproduces every internal vanilla fake-player interaction. Where the public API does not expose a direct weapon action, the modern combat layer uses the documented vanilla model and server-side motion/damage handling instead of treating Mace or Spear as renamed swords.


## 1.8.0 highlights

This release adds the expanded modern PvP layer used by the fork: Elytra Mace/Spear profiles, server-aware damage, real per-bot simulated latency, low-health pearl disengage, Wind Charge Reset compatibility, FastCrystals-aware CPvP scheduling, optional Paper attribute-swap simulation, and Cart PvP equipment/controller support.
## Bot types and weapon profiles

Bot types describe the game mode; weapon profiles describe the equipment/combat loadout. They are saved separately in `bot_templates.yml`.

| Bot type | Mode | Default weapon profile |
|---|---|---|
| `NORMAL` | Standard PvP | `SWORD` |
| `CRYSTAL` | Crystal PvP | `SWORD` |
| `CART` | TNT Minecart PvP | `CART` |
| `MACE` | Mace PvP | `MACE` |
| `UHC` | UHC (no natural saturation regeneration for that bot) | `UHC` |
| `WATER` | Water / Riptide equipment preset | `WATER` |

`CPVP` remains accepted as a legacy alias for `CRYSTAL`. `DUMMY` remains available for older template/editor workflows.

Supported weapon profiles include `SWORD`, `MACE`, `MACE_ELYTRA`, `SPEAR`, `SPEAR_ELYTRA`, `SPEAR_MACE`, `CART`, `CART_ELYTRA`, `UHC`, and `WATER`.

Example template:

```yaml
bot-type: MACE
weapon-profile: MACE_ELYTRA
settings:
  pvp-enabled: true
```

The Water profile equips a Riptide trident and water utility; Riptide only propels the player while wet. UHC natural regeneration is cancelled per bot, rather than changing the world's gamerule for every player.


## Template-aware difficulty

PracticeBot reads the difficulty and kit directly from template keys in the existing
`<kit>_<difficulty>_<player>` format. The kit and tier are stored on the Citizens
bot trait, so all templates use the same difficulty system without duplicating
per-player tuning blocks.

Supported tiers are `EASY`, `NORMAL`, `HARD`, `EXPERT`, `HACKER`,
`MASTER`, and `NIGHTMARE`. This works with the existing template prefixes
(`tank`, `axeandshield`, `nethpot`, `smpkit`, `diamondpot`, `bowpvp`,
`beast`, `diasmp`, `cpvpffa`, and `cartpvp`) and future prefixes such as
`builduhc`, `macepvp`, `macerocket`, `spearelytra`, and `spearmace`.

Difficulty values are resolved in this order:

1. `template-difficulty.kits.<kit>.<tier>` for a kit-specific override.
2. `template-difficulty.profiles.<profile>.<tier>` for NORMAL, CRYSTAL, CART,
   MACE, SPEAR, UHC, or WATER behavior.
3. `template-difficulty.defaults.<tier>` for shared reaction, simulated latency,
   aim, movement, mistakes, attack delays, and pearl decisions.
4. Existing config values as backward-compatible fallbacks.

For example, an optional `template-difficulty.kits.bowpvp.EASY` section can
change only Easy Bow PvP aim, while all other kits continue using the shared
Easy values. New kit prefixes work automatically; no Java changes are required
just to add another kit override.

Mace retains its existing `modern-weapons.mace.difficulty-levels` settings as
legacy fallbacks. Mace and Spear Elytra profiles and Cart PvP have additional
per-tier movement/launch tuning. Vanilla Mace damage and Spear kinetic weapon
thresholds are not changed by the difficulty system.

Templates without a valid tier in their key (including direct `/spawnbot`
spawns) fall back to their existing combat aggression setting.
