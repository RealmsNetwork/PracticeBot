# Changelog

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
