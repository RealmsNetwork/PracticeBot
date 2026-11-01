# Changelog

## Unreleased

## 1.8.3

- Routed Mace normal hits and falling smashes through Minecraft's native server attack pipeline so vanilla controls smash particles, sounds, damage, knockback, enchantments, and Wind Burst; removed the fake critical-particle/damage fallback.
- Added regression coverage for the vanilla 1.5-block smash threshold and invalid/blocked fall states.
- Added template-aware difficulty resolution for every `<kit>_<difficulty>_<player>` template key.
- Applied shared tier tuning for reaction, simulated latency, aim, movement, mistakes, attack delays, and pearl decisions across standard, Crystal, Cart, Mace, Spear, UHC, and Water profiles.
- Added optional per-kit overrides and profile-specific Cart, Mace Elytra, and Spear Elytra tuning.
- Added config v8 migration with additive defaults that preserve existing user values.
- Kept direct bot spawns on the existing combat-aggression fallback and preserved vanilla Mace/Spear damage mechanics.
- Reset transient Mace, Spear, Elytra, Cart, and tactical combat state whenever a Citizens bot respawns, preventing stale Wind Charge/flight state from disabling its AI.

## 1.8.2

- Fixed ground Mace launch to fire a real Wind Charge projectile downward and use the server's native burst physics and particles instead of consuming the item and faking launch velocity.
- Added airborne Mace target tracking, line-of-sight/ray-hit validation, configurable imperfect aim, and fresh Wind Burst follow-up attack state.
- Prevented the generic Wind Charge reset from stealing Mace attack charges during a Mace dive.
- Added first-class NORMAL, CRYSTAL, CART, MACE, UHC, and WATER bot types while retaining CPVP as a legacy config/API alias.
- Added a distinct PracticeBotWeaponProfile API enum, template persistence, editor selection, and built-in CART/MACE/UHC/WATER/MACE_ELYTRA/SPEAR_ELYTRA/SPEAR_MACE templates.
- Added hybrid Spear/Mace hotbar switching, UHC/Water equipment kits, and per-type spawn permissions/commands.
- Added additive config and default-inventory migrations for the new Mace aim and bot kits.

## 1.8.1

- Fixed a Crystal PvP block-candidate scan crash caused by casting a `LinkedHashSet` to `List`.
- Kept the candidate Y-levels as an ordered `Set<Integer>` and iterated them using their actual collection type.

## 1.8.0

- Added Elytra Mace and Elytra Spear profiles.
- Added Cart PvP and Cart Elytra profiles.
- Added server-aware live attack/armor attribute damage handling.
- Corrected Mace smash damage to use the published vanilla falloff without an extra critical multiplier.
- Added exact published Netherite Spear kinetic thresholds and multiplier.
- Added 10-tick Spear contact cooldown handling and charge phase knockback/dismount thresholds.
- Added per-bot simulated ping and reaction/movement/aim humanization.
- Added probabilistic small misplays to avoid deterministic combat behavior.
- Added low-health pearl disengage.
- Added Wind Charge Reset compatibility and local fallback.
- Added FastCrystals-aware CPvP artificial-delay scaling.
- Added Paper unsupported equipment-update detection and optional attribute-swap simulation.
- Added additive config migration for config.yml and default_inv.yml.

## 1.7.0

- Added a dedicated modern Mace and Spear combat layer.
- Added `/spawnbot mace`.
- Added `/spawnbot spear`.
- Added Mace smash fall-distance scaling using the current vanilla falloff tiers.
- Added Mace Density and Breach handling.
- Added Mace Wind Burst chaining and Wind Charge launch planning.
- Added Spear Jab and kinetic Charge behavior.
- Added Spear minimum/maximum reach handling.
- Added configurable Spear kinetic velocity scaling.
- Added Spear Lunge movement and hunger handling.
- Added shield-aware modern weapon decisions.
- Added modern weapon runtime state to Citizens bot traits.
- Added modern weapon kit configuration in `default_inv.yml`.
- Added Java 21 build/test GitHub Actions.
- Added automatic GitHub Release publishing for `v*.*.*` tags.
- Updated project author metadata to THEMPGUY.
- Updated project URL to https://github.com/THEMPGUYAlt.
