package com.sheldera.practicebot.api;

import com.sheldera.practicebot.api.bot.PracticeBotHandle;
import com.sheldera.practicebot.api.cpvp.CpvpSettingsUpdateRequest;
import com.sheldera.practicebot.api.inventory.BotInventoryProfile;
import com.sheldera.practicebot.api.spawn.BotSpawnRequest;
import com.sheldera.practicebot.api.template.BotTemplateView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public interface PracticeBotApi {
   String getApiVersion();

   Optional<PracticeBotHandle> getBotByNpcUuid(UUID var1);

   Optional<PracticeBotHandle> getBotByEntityUuid(UUID var1);

   List<PracticeBotHandle> getActiveBots();

   Optional<BotTemplateView> getTemplate(String var1);

   List<BotTemplateView> getTemplates();

   /** Creates a new template using PracticeBot defaults. */
   BotTemplateView createTemplate(String templateKey);

   /** Creates an independently editable copy of an existing template. */
   BotTemplateView cloneTemplate(String sourceKey, String newKey);

   /** Deletes a custom template. Bundled defaults cannot be deleted. */
   boolean deleteTemplate(String templateKey);

   /** Names of saved bot inventory layouts. */
   List<String> getInventoryProfiles();

   /** Saves the current equipment and storage contents of an active bot as a reusable layout. */
   void saveInventoryProfile(String profileKey, UUID botNpcUuid);

   /** Saves a reusable layout from any Bukkit player inventory. */
   void saveInventoryProfile(String profileKey, PlayerInventory inventory);

   /** Saves a layout from explicit storage, armor, offhand and selected-slot data. */
   void saveInventoryProfile(String profileKey, ItemStack[] storage, ItemStack[] armor, ItemStack offhand, int heldSlot);

   /** Reads a defensive copy of a saved layout. */
   Optional<BotInventoryProfile> getInventoryProfile(String profileKey);

   /** Applies a saved layout directly to a spawned bot. */
   void applyInventoryProfile(UUID botNpcUuid, String profileKey);

   /** Returns the layout key assigned to a template, if any. */
   Optional<String> getTemplateInventoryProfile(String templateKey);

   /** Deletes a saved inventory layout. */
   boolean deleteInventoryProfile(String profileKey);

   /** Assigns a saved layout to a template so every spawned bot receives it. */
   void setTemplateInventoryProfile(String templateKey, String profileKey);

   /** Removes the saved layout assignment from a template. */
   boolean clearTemplateInventoryProfile(String templateKey);

   PracticeBotHandle spawnBot(BotSpawnRequest var1);

   boolean despawnBot(UUID var1);

   PracticeBotHandle applyTemplate(UUID var1, String var2, UUID var3);

   PracticeBotHandle updateCpvpSettings(UUID var1, CpvpSettingsUpdateRequest var2);
}
