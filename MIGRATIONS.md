# Configuration migrations

PracticeBot migrations are additive.

The plugin preserves existing user values. New settings are only added when the path is missing, and config migration creates a backup before changing an existing config.

## config.yml

Version 1 -> 2 added the first modern Mace/Spear, humanization, Wind Charge Reset, Elytra, FastCrystals, attribute-swap, Cart PvP and weapon-kit settings.

Version 2 -> 3 added the published Spear kinetic thresholds, human misplay settings, pearl disengage settings and additional Cart PvP settings.

The backup created before a schema upgrade is:

plugins/PracticeBot/config.yml.v3.pre-migration.bak

## default_inv.yml

Inventory configuration has its own schema:

inventory-config-version: 2

Migration adds modern weapon, mobility, Elytra and Cart PvP defaults without overwriting existing kit values.

